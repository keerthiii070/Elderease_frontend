<?php
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) $data = $_POST;

$email = trim($data['email'] ?? '');

if (empty($email)) {
    echo json_encode(["status" => false, "message" => "Email required"]);
    exit;
}

$stmt = $conn->prepare(
    "UPDATE users 
     SET is_deleted = 1, deleted_at = NOW() 
     WHERE email = ?"
);
$stmt->bind_param("s", $email);
$stmt->execute();

echo json_encode([
    "status" => true,
    "message" => "Account deactivated. You can restore within 30 days."
]);
