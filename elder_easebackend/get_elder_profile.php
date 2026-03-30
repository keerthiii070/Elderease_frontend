<?php
header("Content-Type: application/json");
include "db.php";

$email = $_GET['email'] ?? '';

if (!$email) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "SELECT 
        full_name,
        email,
        phone,
        age,
        weight_kg,
        height_cm,
        bmi,
        blood_group,
        health_conditions,
        profile_image
     FROM users
     WHERE email = ?
     LIMIT 1"
);

$stmt->bind_param("s", $email);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "User not found"
    ]);
    exit;
}

$data = $res->fetch_assoc();

echo json_encode([
    "status" => true,
    "profile" => $data
]);
