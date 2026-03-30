<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");
include "db.php";

$email = $_GET['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

$today = date("Y-m-d");

$stmt = $conn->prepare(
    "SELECT
        pose_id,
        pose_title,
        duration_minutes,
        created_at
     FROM yoga_logs
     WHERE email = ?
       AND DATE(created_at) = ?
     ORDER BY created_at ASC"
);

$stmt->bind_param("ss", $email, $today);
$stmt->execute();

$res = $stmt->get_result();

$totalMinutes = 0;
$poses = [];

while ($row = $res->fetch_assoc()) {
    $totalMinutes += (int)$row['duration_minutes'];
    $poses[] = $row;
}

echo json_encode([
    "status" => true,
    "date" => $today,
    "poses_completed" => count($poses),
    "total_minutes" => $totalMinutes,
    "poses" => $poses
]);
