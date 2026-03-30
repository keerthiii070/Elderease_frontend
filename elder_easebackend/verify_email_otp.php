<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) $data = $_POST;

$email = trim($data['email'] ?? '');
$otp   = trim($data['otp'] ?? '');

if ($email === '' || $otp === '') {
    echo json_encode([
        "status" => false,
        "message" => "Email and OTP required"
    ]);
    exit;
}

$stmt = $conn->prepare(
    "SELECT id, email_otp, email_otp_expiry 
     FROM users WHERE email = ?"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email not found"
    ]);
    exit;
}

$user = $result->fetch_assoc();

if ($otp !== $user['email_otp']) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid OTP"
    ]);
    exit;
}

if (date("Y-m-d H:i:s") > $user['email_otp_expiry']) {
    echo json_encode([
        "status" => false,
        "message" => "OTP expired"
    ]);
    exit;
}

/* MARK VERIFIED */
$verify = $conn->prepare(
    "UPDATE users
     SET email_verified = 1,
         email_otp = NULL,
         email_otp_expiry = NULL
     WHERE id = ?"
);
$verify->bind_param("i", $user['id']);
$verify->execute();

echo json_encode([
    "status" => true,
    "message" => "Email verified successfully"
]);
