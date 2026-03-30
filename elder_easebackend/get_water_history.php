<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header("Content-Type: application/json");
include "db.php";

/* ----------------------------------------
   READ INPUT (JSON OR FORM-DATA)
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    $data = $_POST;
}

/* ----------------------------------------
   GET INPUT
---------------------------------------- */
$email = $data['email'] ?? '';
$days  = $data['days'] ?? 7; // default last 7 days

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

$days = (int)$days;
if ($days < 1 || $days > 30) {
    $days = 7;
}

/* ----------------------------------------
   FETCH WATER HISTORY
---------------------------------------- */
$stmt = $conn->prepare(
    "SELECT 
        intake_date,
        SUM(amount_ml) AS total_ml
     FROM water_intake
     WHERE email = ?
       AND intake_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
     GROUP BY intake_date
     ORDER BY intake_date ASC"
);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "message" => "SQL prepare failed",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param("si", $email, $days);
$stmt->execute();
$result = $stmt->get_result();

$history = [];

while ($row = $result->fetch_assoc()) {
    $history[] = [
        "date" => $row['intake_date'],
        "total_ml" => (int)$row['total_ml']
    ];
}

/* ----------------------------------------
   SUCCESS RESPONSE
---------------------------------------- */
echo json_encode([
    "status" => true,
    "days" => $days,
    "history" => $history
]);
