<?php
header("Content-Type: application/json");
include "db.php";

$email = $_GET['email'] ?? '';

$stmt = $conn->prepare(
    "SELECT bmi, category, height_cm, weight_kg, created_at
     FROM bmi_records
     WHERE email = ?
     ORDER BY created_at DESC"
);
$stmt->bind_param("s", $email);
$stmt->execute();

$rows = [];
$res = $stmt->get_result();
while ($row = $res->fetch_assoc()) {
    $rows[] = $row;
}

echo json_encode([
    "status" => true,
    "history" => $rows
]);
