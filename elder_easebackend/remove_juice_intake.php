<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email     = $data['email'] ?? '';
$amountMl  = (int)($data['amount_ml'] ?? 0);

if (empty($email) || $amountMl <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email and valid amount required"
    ]);
    exit;
}

$today = date("Y-m-d");

/* FIND LAST ENTRY WITH SAME AMOUNT */
$stmt = $conn->prepare(
    "SELECT id 
     FROM juice_intake 
     WHERE email = ? 
       AND intake_date = ? 
       AND amount_ml = ?
     ORDER BY id DESC
     LIMIT 1"
);
$stmt->bind_param("ssi", $email, $today, $amountMl);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "No matching juice intake found to remove"
    ]);
    exit;
}

$row = $result->fetch_assoc();
$entryId = $row['id'];

/* DELETE ONLY THAT ENTRY */
$del = $conn->prepare("DELETE FROM juice_intake WHERE id = ?");
$del->bind_param("i", $entryId);
$del->execute();

/* GET UPDATED TOTAL */
$totalStmt = $conn->prepare(
    "SELECT COALESCE(SUM(amount_ml),0) AS total
     FROM juice_intake
     WHERE email = ? AND intake_date = ?"
);
$totalStmt->bind_param("ss", $email, $today);
$totalStmt->execute();
$newTotal = (int)$totalStmt->get_result()->fetch_assoc()['total'];

echo json_encode([
    "status" => true,
    "message" => "Juice intake removed",
    "removed" => $amountMl,
    "todayTotal" => $newTotal
]);
