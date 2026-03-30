<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* -------------------------------
   READ INPUT (GET)
-------------------------------- */
$email = $_GET['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* -------------------------------
   FETCH PAIN LOGS
-------------------------------- */
$sql = "
    SELECT 
        id,
        pain_area,
        pain_points,
        stiffness_minutes,
        pain_severity,
        pain_level,
        created_at
    FROM pain_logs
    WHERE email = ?
    ORDER BY created_at DESC
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("s", $email);
$stmt->execute();

$res = $stmt->get_result();

$logs = [];

while ($row = $res->fetch_assoc()) {

    // Decode pain points safely
    $decodedPoints = json_decode($row['pain_points'], true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        $decodedPoints = [];
    }

    $logs[] = [
        "id" => (int)$row['id'],
        "pain_area" => $row['pain_area'],
        "pain_points" => $decodedPoints,
        "stiffness_minutes" => $row['stiffness_minutes'],
        "pain_severity" => (int)$row['pain_severity'],
        "pain_level" => $row['pain_level'],
        "created_at" => $row['created_at']
    ];
}

/* -------------------------------
   RESPONSE
-------------------------------- */
echo json_encode([
    "status" => true,
    "count" => count($logs),
    "logs" => $logs
]);
