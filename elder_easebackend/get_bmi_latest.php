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
    "SELECT bmi, category, age, height_cm, weight_kg, created_at
     FROM bmi_records
     WHERE email = ?
     ORDER BY created_at DESC
     LIMIT 1"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows === 0) {
    echo json_encode([
        "status" => true,
        "data" => null
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "data" => $res->fetch_assoc()
]);
