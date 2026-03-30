<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* -------------------------------
   READ INPUT
-------------------------------- */
$email = $_GET['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* -------------------------------
   TODAY DATE
-------------------------------- */
$today = date("Y-m-d");

/* -------------------------------
   FETCH TODAY'S LATEST PAIN LOG
-------------------------------- */
$sql = "
    SELECT
        id,
        pain_area,
        pain_points,
        stiffness_minutes,
        pain_severity,
        pain_level,
        created_at
    FROM pain_logs
    WHERE email = ?
      AND DATE(created_at) = ?
    ORDER BY created_at DESC
    LIMIT 1
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("ss", $email, $today);
$stmt->execute();

$res = $stmt->get_result();

/* -------------------------------
   NO DATA FOR TODAY
-------------------------------- */
if ($res->num_rows === 0) {
    echo json_encode([
        "status" => true,
        "data" => null
    ]);
    exit;
}

/* -------------------------------
   FORMAT RESPONSE
-------------------------------- */
$row = $res->fetch_assoc();

/* Decode pain points safely */
$painPoints = json_decode($row['pain_points'], true);
if (json_last_error() !== JSON_ERROR_NONE) {
    $painPoints = [];
}

$response = [
    "id" => (int)$row['id'],
    "pain_area" => $row['pain_area'],
    "pain_points" => $painPoints,
    "stiffness_minutes" => $row['stiffness_minutes'],
    "pain_severity" => (int)$row['pain_severity'],
    "pain_level" => $row['pain_level'],
    "created_at" => $row['created_at']
];

echo json_encode([
    "status" => true,
    "data" => $response
]);
