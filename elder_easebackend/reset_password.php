<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* ----------------------------------------
   READ INPUT (JSON / FORM)
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) {
    $data = $_POST;
}

$email       = trim($data['email'] ?? '');
$newPassword = trim($data['password'] ?? '');

if (empty($email) || empty($newPassword)) {
    echo json_encode([
        "status" => false,
        "message" => "Email and password are required"
    ]);
    exit;
}

/* ----------------------------------------
   VALIDATE PASSWORD
---------------------------------------- */
if (strlen($newPassword) < 6) {
    echo json_encode([
        "status" => false,
        "message" => "Password must be at least 6 characters"
    ]);
    exit;
}

/* ----------------------------------------
   CHECK USER EXISTS
---------------------------------------- */
$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "User not found"
    ]);
    exit;
}

$user = $result->fetch_assoc();

/* ----------------------------------------
   UPDATE PASSWORD (HASHED)
---------------------------------------- */
$hashedPassword = password_hash($newPassword, PASSWORD_BCRYPT);

$update = $conn->prepare(
    "UPDATE users 
     SET password = ?, otp = NULL, otp_expiry = NULL 
     WHERE id = ?"
);
$update->bind_param("si", $hashedPassword, $user['id']);
$update->execute();

/* ----------------------------------------
   SUCCESS RESPONSE
---------------------------------------- */
echo json_encode([
    "status" => true,
    "message" => "Password reset successfully"
]);
