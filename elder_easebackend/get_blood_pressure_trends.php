<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email = $data['email'] ?? '';
$days  = (int)($data['days'] ?? 7); // 1, 7, 30

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

$fromDate = date("Y-m-d", strtotime("-$days days"));

$stmt = $conn->prepare(
    "SELECT systolic, diastolic, pulse,
            reading_date, reading_time
     FROM blood_pressure
     WHERE email = ?
     AND reading_date >= ?
     ORDER BY reading_date ASC, reading_time ASC"
);
$stmt->bind_param("ss", $email, $fromDate);
$stmt->execute();
$result = $stmt->get_result();

$records = [];

while ($row = $result->fetch_assoc()) {
    $timestamp = strtotime($row['reading_date']." ".$row['reading_time']) * 1000;

    $records[] = [
        "systolic" => (int)$row['systolic'],
        "diastolic" => (int)$row['diastolic'],
        "pulse" => (int)$row['pulse'],
        "timestamp" => $timestamp
    ];
}

echo json_encode([
    "status" => true,
    "days" => $days,
    "records" => $records
]);
