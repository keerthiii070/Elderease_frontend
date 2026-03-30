<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email      = $data['email'] ?? '';
$pose_id    = (int)($data['pose_id'] ?? 0);
$pose_title = $data['pose_title'] ?? '';
$duration   = (int)($data['duration_minutes'] ?? 0);

if (
    empty($email) ||
    $pose_id <= 0 ||
    empty($pose_title) ||
    $duration <= 0
) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid input"
    ]);
    exit;
}

/* INSERT */
$stmt = $conn->prepare(
    "INSERT INTO yoga_logs (email, pose_id, pose_title, duration_minutes)
     VALUES (?,?,?,?)"
);

$stmt->bind_param(
    "sisi",
    $email,
    $pose_id,
    $pose_title,
    $duration
);

if (!$stmt->execute()) {
    echo json_encode([
        "status" => false,
        "error" => $stmt->error
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Yoga pose saved successfully"
]);
