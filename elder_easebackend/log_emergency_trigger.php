<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$email = $data['email'] ?? '';
$lat   = $data['latitude'] ?? null;
$lng   = $data['longitude'] ?? null;

if (!$email) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "INSERT INTO emergency_logs (user_email, latitude, longitude)
     VALUES (?, ?, ?)"
);

$stmt->bind_param("sdd", $email, $lat, $lng);
$stmt->execute();

echo json_encode([
    "status" => true,
    "message" => "Emergency logged"
]);
