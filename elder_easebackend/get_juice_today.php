<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email = $data['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

$today = date("Y-m-d");

/* 🔥 DAILY JUICE LIMIT = 400 ML */
$DAILY_JUICE_LIMIT = 400;

/* GET TODAY TOTAL */
$stmt = $conn->prepare(
    "SELECT COALESCE(SUM(amount_ml),0) AS total
     FROM juice_intake
     WHERE email = ? AND intake_date = ?"
);
$stmt->bind_param("ss", $email, $today);
$stmt->execute();
$total = (int)$stmt->get_result()->fetch_assoc()['total'];

/* RESPONSE */
echo json_encode([
    "status" => true,
    "todayTotal" => $total,
    "dailyLimit" => $DAILY_JUICE_LIMIT,
    "remaining" => max(0, $DAILY_JUICE_LIMIT - $total),
    "date" => $today
]);
