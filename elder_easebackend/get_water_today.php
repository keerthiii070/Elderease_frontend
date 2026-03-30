<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email = $data['email'] ?? '';
if (empty($email)) {
    echo json_encode(["status"=>false,"message"=>"Email required"]);
    exit;
}

$today = date("Y-m-d");

/* TOTAL */
$stmt = $conn->prepare(
    "SELECT COALESCE(SUM(amount_ml),0) AS total
     FROM water_intake
     WHERE email=? AND intake_date=?"
);
$stmt->bind_param("ss",$email,$today);
$stmt->execute();
$total = (int)$stmt->get_result()->fetch_assoc()['total'];

/* DAILY GOAL (fallback 2000) */
$goalStmt = $conn->prepare(
    "SELECT COALESCE(daily_water_goal,2000) AS goal
     FROM users WHERE email=?"
);
$goalStmt->bind_param("s",$email);
$goalStmt->execute();
$goal = (int)$goalStmt->get_result()->fetch_assoc()['goal'];

echo json_encode([
    "status"=>true,
    "todayTotal"=>$total,
    "dailyTarget"=>$goal
]);
