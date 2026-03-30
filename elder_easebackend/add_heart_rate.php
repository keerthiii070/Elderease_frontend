<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email = $data['email'] ?? '';
$bpm   = (int)($data['bpm'] ?? 0);

if (empty($email) || $bpm < 40 || $bpm > 190) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid heart rate data"
    ]);
    exit;
}

/* CLASSIFY STATUS */
if ($bpm < 60) {
    $status = "LOW";
} elseif ($bpm <= 100) {
    $status = "HEALTHY";
} else {
    $status = "HIGH";
}

$reading_date = date("Y-m-d");
$reading_time = date("H:i:s");

/* INSERT */
$stmt = $conn->prepare(
    "INSERT INTO heart_rate 
     (email, bpm, status, reading_date, reading_time)
     VALUES (?, ?, ?, ?, ?)"
);
$stmt->bind_param("sisss", $email, $bpm, $status, $reading_date, $reading_time);

if (!$stmt->execute()) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to save heart rate"
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Heart rate saved",
    "data" => [
        "bpm" => $bpm,
        "status" => $status,
        "date" => $reading_date,
        "time" => date("h:i A")
    ]
]);
