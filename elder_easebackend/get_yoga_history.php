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
   FETCH YOGA HISTORY
-------------------------------- */
$sql = "
    SELECT
        DATE(created_at) AS yoga_date,
        pose_id,
        pose_title,
        duration_minutes,
        created_at
    FROM yoga_logs
    WHERE email = ?
    ORDER BY created_at DESC
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("s", $email);
$stmt->execute();
$res = $stmt->get_result();

/* -------------------------------
   FORMAT DATA
-------------------------------- */
$history = [];

while ($row = $res->fetch_assoc()) {
    $date = $row['yoga_date'];

    if (!isset($history[$date])) {
        $history[$date] = [
            "date" => $date,
            "total_minutes" => 0,
            "poses" => []
        ];
    }

    $history[$date]["total_minutes"] += (int)$row['duration_minutes'];

    $history[$date]["poses"][] = [
        "pose_id" => (int)$row['pose_id'],
        "pose_title" => $row['pose_title'],
        "duration_minutes" => (int)$row['duration_minutes'],
        "time" => date("h:i A", strtotime($row['created_at']))
    ];
}

/* -------------------------------
   RESPONSE
-------------------------------- */
echo json_encode([
    "status" => true,
    "count" => count($history),
    "history" => array_values($history)
]);
