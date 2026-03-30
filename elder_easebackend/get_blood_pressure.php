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

/* FETCH HISTORY */
$stmt = $conn->prepare(
    "SELECT systolic, diastolic, pulse,
            reading_date, reading_time
     FROM blood_pressure
     WHERE email = ?
     ORDER BY reading_date DESC, reading_time DESC"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

$history = [];

while ($row = $result->fetch_assoc()) {
    $timestamp = strtotime($row['reading_date']." ".$row['reading_time']) * 1000;

    $history[] = [
        "systolic" => (int)$row['systolic'],
        "diastolic" => (int)$row['diastolic'],
        "pulse" => (int)$row['pulse'],
        "timestamp" => $timestamp
    ];
}

echo json_encode([
    "status" => true,
    "records" => $history
]);
