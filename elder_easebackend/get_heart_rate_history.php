<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* GET EMAIL */
$email = $_GET['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

/* TODAY DATE */
$today = date("Y-m-d");

/* FETCH LATEST HEART RATE FOR TODAY */
$stmt = $conn->prepare(
    "SELECT bpm, status, reading_time
     FROM heart_rate
     WHERE email = ?
       AND reading_date = ?
     ORDER BY reading_time DESC
     LIMIT 1"
);

$stmt->bind_param("ss", $email, $today);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => true,
        "data" => null
    ]);
    exit;
}

$row = $result->fetch_assoc();

/* SUCCESS */
echo json_encode([
    "status" => true,
    "data" => [
        "bpm" => (int)$row['bpm'],
        "status" => $row['status'],
        "time" => date("h:i A", strtotime($row['reading_time']))
    ]
]);
