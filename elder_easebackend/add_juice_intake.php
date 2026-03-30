<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email    = $data['email'] ?? '';
$amountMl = (int)($data['amount_ml'] ?? 0);

if (empty($email) || $amountMl <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email and valid amount required"
    ]);
    exit;
}

/* 🔥 DAILY JUICE LIMIT SET TO 400 ML */
$DAILY_JUICE_LIMIT = 400;
$today = date("Y-m-d");

/* CURRENT TOTAL FOR TODAY */
$stmt = $conn->prepare(
    "SELECT COALESCE(SUM(amount_ml),0) AS total
     FROM juice_intake
     WHERE email = ? AND intake_date = ?"
);
$stmt->bind_param("ss", $email, $today);
$stmt->execute();
$currentTotal = (int)$stmt->get_result()->fetch_assoc()['total'];

/* LIMIT CHECK */
if ($currentTotal >= $DAILY_JUICE_LIMIT) {
    echo json_encode([
        "status" => false,
        "message" => "Daily juice limit reached",
        "todayTotal" => $currentTotal,
        "dailyLimit" => $DAILY_JUICE_LIMIT,
        "remaining" => 0
    ]);
    exit;
}

if ($currentTotal + $amountMl > $DAILY_JUICE_LIMIT) {
    echo json_encode([
        "status" => false,
        "message" => "Exceeds daily juice limit",
        "todayTotal" => $currentTotal,
        "dailyLimit" => $DAILY_JUICE_LIMIT,
        "remaining" => $DAILY_JUICE_LIMIT - $currentTotal
    ]);
    exit;
}

/* INSERT JUICE ENTRY */
$time = date("H:i:s");
$ins = $conn->prepare(
    "INSERT INTO juice_intake (email, amount_ml, intake_date, intake_time)
     VALUES (?, ?, ?, ?)"
);
$ins->bind_param("siss", $email, $amountMl, $today, $time);
$ins->execute();

$newTotal = $currentTotal + $amountMl;

/* RESPONSE */
echo json_encode([
    "status" => true,
    "message" => "Juice intake added",
    "added" => $amountMl,
    "todayTotal" => $newTotal,
    "dailyLimit" => $DAILY_JUICE_LIMIT,
    "remaining" => $DAILY_JUICE_LIMIT - $newTotal,
    "time" => date("h:i A")
]);
