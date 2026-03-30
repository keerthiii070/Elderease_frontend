<?php
header("Content-Type: application/json");
include "db.php";

$email = $_GET['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email required"
    ]);
    exit;
}

/* FETCH PAIN LOGS */
$stmt = $conn->prepare(
    "SELECT 
        id,
        pain_area,
        pain_points,
        stiffness_minutes,
        pain_severity,
        pain_level,
        created_at
     FROM pain_logs
     WHERE email = ?
     ORDER BY created_at DESC"
);

$stmt->bind_param("s", $email);
$stmt->execute();

$result = $stmt->get_result();
$logs = [];

while ($row = $result->fetch_assoc()) {
    $row['pain_points'] = json_decode($row['pain_points'], true); // decode JSON points
    $logs[] = $row;
}

echo json_encode([
    "status" => true,
    "count" => count($logs),
    "logs" => $logs
]);
