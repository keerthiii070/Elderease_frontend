<?php
header("Content-Type: application/json");
include "db.php";

/* ----------------------------------------
   READ INPUT (JSON OR FORM-DATA)
---------------------------------------- */
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

/* ----------------------------------------
   TODAY DATE
---------------------------------------- */
$today = date("Y-m-d");

/* ----------------------------------------
   GET USER DAILY GOAL
---------------------------------------- */
$goalStmt = $conn->prepare(
    "SELECT COALESCE(daily_water_goal, 2000) AS goal
     FROM users WHERE email = ?"
);
$goalStmt->bind_param("s", $email);
$goalStmt->execute();
$goalRow = $goalStmt->get_result()->fetch_assoc();

$userGoal = (int)$goalRow['goal'];

/* ----------------------------------------
   ABSOLUTE SAFETY LIMIT (MAX 5000)
---------------------------------------- */
$MAX_ABSOLUTE_LIMIT = 5000;
$dailyLimit = min($userGoal, $MAX_ABSOLUTE_LIMIT);

/* ----------------------------------------
   CURRENT TOTAL
---------------------------------------- */
$totalStmt = $conn->prepare(
    "SELECT COALESCE(SUM(amount_ml),0) AS total
     FROM water_intake
     WHERE email = ? AND intake_date = ?"
);
$totalStmt->bind_param("ss", $email, $today);
$totalStmt->execute();
$currentTotal = (int)$totalStmt->get_result()->fetch_assoc()['total'];

/* ----------------------------------------
   LIMIT CHECKS
---------------------------------------- */
if ($currentTotal >= $dailyLimit) {
    echo json_encode([
        "status" => false,
        "message" => "Daily water goal reached",
        "dailyGoal" => $dailyLimit,
        "todayTotal" => $currentTotal,
        "remaining" => 0
    ]);
    exit;
}

if ($currentTotal + $amountMl > $dailyLimit) {
    echo json_encode([
        "status" => false,
        "message" => "This intake exceeds your daily goal",
        "dailyGoal" => $dailyLimit,
        "todayTotal" => $currentTotal,
        "remaining" => $dailyLimit - $currentTotal
    ]);
    exit;
}

/* ----------------------------------------
   INSERT WATER INTAKE
---------------------------------------- */
$time = date("H:i:s");

$insertStmt = $conn->prepare(
    "INSERT INTO water_intake (email, amount_ml, intake_date, intake_time)
     VALUES (?, ?, ?, ?)"
);
$insertStmt->bind_param("siss", $email, $amountMl, $today, $time);
$insertStmt->execute();

$newTotal = $currentTotal + $amountMl;

/* ----------------------------------------
   SUCCESS RESPONSE
---------------------------------------- */
echo json_encode([
    "status" => true,
    "message" => "Water intake added",
    "added" => $amountMl,
    "dailyGoal" => $dailyLimit,
    "todayTotal" => $newTotal,
    "remaining" => $dailyLimit - $newTotal,
    "time" => date("h:i A")
]);
