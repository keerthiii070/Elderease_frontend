<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email = $data['email'] ?? '';
$goal  = (int)($data['daily_target'] ?? 0);

if (empty($email) || $goal < 500 || $goal > 5000) {
    echo json_encode([
        "status" => false,
        "message" => "Daily goal must be between 500 and 5000 ml"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "UPDATE users SET daily_water_goal = ? WHERE email = ?"
);
$stmt->bind_param("is", $goal, $email);
$stmt->execute();

echo json_encode([
    "status" => true,
    "dailyTarget" => $goal
]);
