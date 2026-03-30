<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* READ INPUT (JSON / FORM DATA) */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email     = $data['email'] ?? '';
$systolic  = (int)($data['systolic'] ?? 0);
$diastolic = (int)($data['diastolic'] ?? 0);
$pulse     = (int)($data['pulse'] ?? 0);

if (
    empty($email) ||
    $systolic <= 0 ||
    $diastolic <= 0 ||
    $pulse <= 0
) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid input data"
    ]);
    exit;
}

/* DATE & TIME (STRICT FORMAT) */
$reading_date = date("Y-m-d");   // DATE
$reading_time = date("H:i:s");   // TIME

/* INSERT */
$stmt = $conn->prepare(
    "INSERT INTO blood_pressure
    (email, systolic, diastolic, pulse, reading_date, reading_time)
    VALUES (?, ?, ?, ?, ?, ?)"
);

$stmt->bind_param(
    "siiiss",
    $email,
    $systolic,
    $diastolic,
    $pulse,
    $reading_date,
    $reading_time
);

if (!$stmt->execute()) {
    echo json_encode([
        "status" => false,
        "message" => "Database insert failed"
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Blood pressure saved",
    "data" => [
        "systolic" => $systolic,
        "diastolic" => $diastolic,
        "pulse" => $pulse,
        "date" => $reading_date,
        "time" => date("h:i A")
    ]
]);
