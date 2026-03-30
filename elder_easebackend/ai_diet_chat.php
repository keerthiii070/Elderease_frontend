<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");
include "db.php";

/* ================= CONFIG ================= */
$API_KEY     = "AIzaSyDddqmiP9of4pX9PhHzuNexXHolovEtvvA";
$MODE        = "DIET";
$DAILY_LIMIT = 10;

/* ================= READ INPUT ================= */
$data = json_decode(file_get_contents("php://input"), true);
$email  = trim($data['email'] ?? '');
$prompt = trim($data['prompt'] ?? '');

if ($email === "" || $prompt === "") {
    echo json_encode([
        "status" => false,
        "message" => "Email and prompt are required"
    ]);
    exit;
}

$promptLower = strtolower($prompt);
$today = date("Y-m-d");

/* ================= FETCH USER BASIC INFO ================= */
$name = null;
$age = null;
$weight = null;
$bmi = null;

/* User name */
$stmt = $conn->prepare(
    "SELECT full_name FROM users WHERE email = ? LIMIT 1"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$userRow = $stmt->get_result()->fetch_assoc();
$name = $userRow['full_name'] ?? null;

/* BMI / Weight */
$stmt = $conn->prepare(
    "SELECT age, weight_kg, bmi
     FROM bmi_records
     WHERE email = ?
     ORDER BY created_at DESC
     LIMIT 1"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$bmiRow = $stmt->get_result()->fetch_assoc();

if ($bmiRow) {
    $age    = $bmiRow['age'];
    $weight = $bmiRow['weight_kg'];
    $bmi    = round($bmiRow['bmi'], 1);
}

/* ================= PERSONAL INFO QUESTIONS (NO AI) ================= */
if (preg_match("/what is my name|my name/i", $promptLower)) {
    echo json_encode([
        "status" => true,
        "reply" => $name
            ? "Your name is **$name** 😊"
            : "I don’t have your name saved yet."
    ]);
    exit;
}

if (str_contains($promptLower, "my weight")) {
    echo json_encode([
        "status" => true,
        "reply" => $weight
            ? "Your current recorded weight is **$weight kg**."
            : "I don’t have your weight recorded yet."
    ]);
    exit;
}

if (str_contains($promptLower, "my bmi")) {
    echo json_encode([
        "status" => true,
        "reply" => $bmi
            ? "Your latest BMI is **$bmi**."
            : "I don’t have your BMI data yet."
    ]);
    exit;
}

/* ================= DAILY LIMIT (AI ONLY) ================= */
$stmt = $conn->prepare(
    "SELECT COUNT(*) AS cnt
     FROM ai_chat_history
     WHERE email = ?
       AND role = 'user'
       AND mode = ?
       AND DATE(created_at) = ?"
);
$stmt->bind_param("sss", $email, $MODE, $today);
$stmt->execute();
$count = (int)$stmt->get_result()->fetch_assoc()['cnt'];

if ($count >= $DAILY_LIMIT) {
    echo json_encode([
        "status" => false,
        "message" => "You’ve reached today’s free AI limit 😊 Please come back tomorrow."
    ]);
    exit;
}

/* ================= GREETING ================= */
$greetings = ["hi","hello","hey","hii","hai","good morning","good evening"];

if (in_array($promptLower, $greetings)) {
    echo json_encode([
        "status" => true,
        "reply" =>
            "Hello 😊 I’m your personal diet assistant.\n\n" .
            "You can ask me about:\n" .
            "• Breakfast, lunch, dinner ideas\n" .
            "• Diabetes-friendly foods\n" .
            "• Foods to avoid\n\n" .
            "How can I help today?",
        "remaining_today" => $DAILY_LIMIT - $count
    ]);
    exit;
}

/* ================= SHORT vs LONG ================= */
$responseStyle = preg_match(
    "/full plan|diet chart|complete plan|weekly plan/i",
    $promptLower
) ? "LONG" : "SHORT";

/* ================= FOLLOW-UP ================= */
$isFollowUp = preg_match(
    "/what about|and lunch|and dinner|then|what next/i",
    $promptLower
);

/* ================= SYSTEM PROMPT ================= */
$systemPrompt = "
You are a friendly senior-focused diet assistant.

User profile:
- Name: " . ($name ?? "Unknown") . "
- Age: " . ($age ?? "Unknown") . "
- Weight: " . ($weight ? "$weight kg" : "Unknown") . "
- BMI: " . ($bmi ?? "Unknown") . "
- Condition: Diabetes

Rules:
- Answer ONLY food & diet questions
- SHORT → 3–4 bullets
- LONG → structured explanation
- Follow-ups → do NOT repeat intro
- No medical diagnosis
- Warm & respectful tone
";

/* ================= BUILD CONTEXT ================= */
$contents = [];
$contents[] = [
    "role" => "user",
    "parts" => [[ "text" => $systemPrompt ]]
];

/* Chat history */
$stmt = $conn->prepare(
    "SELECT role, message
     FROM ai_chat_history
     WHERE email = ?
       AND mode = ?
     ORDER BY created_at DESC
     LIMIT 6"
);
$stmt->bind_param("ss", $email, $MODE);
$stmt->execute();
$history = array_reverse($stmt->get_result()->fetch_all(MYSQLI_ASSOC));

foreach ($history as $row) {
    $contents[] = [
        "role" => ($row['role'] === "assistant" ? "model" : "user"),
        "parts" => [[ "text" => $row['message'] ]]
    ];
}

/* Current user message */
$contents[] = [
    "role" => "user",
    "parts" => [[
        "text" =>
            ($isFollowUp ? "Follow-up.\n" : "") .
            "Style: $responseStyle\nQuestion: $prompt"
    ]]
];

/* ================= GEMINI CALL ================= */
$url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$API_KEY";

$payload = ["contents" => $contents];

$ch = curl_init($url);
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_HTTPHEADER => ["Content-Type: application/json"],
    CURLOPT_POSTFIELDS => json_encode($payload)
]);

$response = curl_exec($ch);
curl_close($ch);

$data = json_decode($response, true);

/* ================= SAFE PARSE ================= */
$reply = "";
if (isset($data['candidates'][0]['content']['parts'])) {
    foreach ($data['candidates'][0]['content']['parts'] as $part) {
        if (!empty($part['text'])) {
            $reply .= $part['text'];
        }
    }
}
$reply = trim($reply);

if ($reply === "") {
    echo json_encode([
        "status" => false,
        "message" => "AI did not respond properly",
        "raw_response" => $data
    ]);
    exit;
}

/* ================= SAVE CHAT ================= */
$stmt = $conn->prepare(
    "INSERT INTO ai_chat_history (email, role, message, mode)
     VALUES (?, ?, ?, ?)"
);

$stmt->bind_param("ssss", $email, $role, $msg, $MODE);
$role = "user";      $msg = $prompt; $stmt->execute();
$role = "assistant"; $msg = $reply;  $stmt->execute();

/* ================= RESPONSE ================= */
echo json_encode([
    "status" => true,
    "reply" => $reply,
    "remaining_today" => $DAILY_LIMIT - ($count + 1)
]);
