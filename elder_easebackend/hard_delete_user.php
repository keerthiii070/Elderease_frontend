<?php
header("Content-Type: application/json");
include "db.php";

$email = $_POST['email'] ?? '';

$stmt = $conn->prepare("DELETE FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();

echo json_encode([
    "status" => true,
    "message" => "User permanently deleted"
]);
