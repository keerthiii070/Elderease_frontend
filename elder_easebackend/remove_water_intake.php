<?php
header("Content-Type: application/json");
include "db.php";

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email = $data['email'] ?? '';
if (empty($email)) {
    echo json_encode(["status"=>false,"message"=>"Email required"]);
    exit;
}

$today = date("Y-m-d");

/* GET LAST ENTRY */
$get = $conn->prepare(
    "SELECT id, amount_ml
     FROM water_intake
     WHERE email=? AND intake_date=?
     ORDER BY intake_time DESC
     LIMIT 1"
);
$get->bind_param("ss",$email,$today);
$get->execute();
$res = $get->get_result();

if ($res->num_rows == 0) {
    echo json_encode(["status"=>false,"message"=>"No intake to remove"]);
    exit;
}

$row = $res->fetch_assoc();

/* DELETE */
$del = $conn->prepare("DELETE FROM water_intake WHERE id=?");
$del->bind_param("i",$row['id']);
$del->execute();

/* NEW TOTAL */
$total = $conn->prepare(
    "SELECT COALESCE(SUM(amount_ml),0) AS total
     FROM water_intake
     WHERE email=? AND intake_date=?"
);
$total->bind_param("ss",$email,$today);
$total->execute();
$newTotal = (int)$total->get_result()->fetch_assoc()['total'];

echo json_encode([
    "status"=>true,
    "removed"=>$row['amount_ml'],
    "todayTotal"=>$newTotal
]);
