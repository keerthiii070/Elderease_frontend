<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* ----------------------------------------
   READ INPUT (ANDROID / POSTMAN / FORM)
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);

if (!is_array($data)) {
    $data = $_POST;
}

$email = trim($data['email'] ?? '');
$otp   = trim($data['otp'] ?? '');

if (empty($email) || empty($otp)) {
    echo json_encode([
        "status" => false,
        "message" => "Email and OTP are required"
    ]);
    exit;
}

/* ----------------------------------------
   FETCH OTP
---------------------------------------- */
$stmt = $conn->prepare(
    "SELECT otp, otp_expiry FROM users WHERE email = ?"
);
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
   VALIDATIONS
---------------------------------------- */
if (empty($user['otp'])) {
    echo json_encode([
        "status" => false,
        "message" => "OTP not requested"
    ]);
    exit;
}

if ($otp !== $user['otp']) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid OTP"
    ]);
    exit;
}

if (date("Y-m-d H:i:s") > $user['otp_expiry']) {
    echo json_encode([
        "status" => false,
        "message" => "OTP expired"
    ]);
    exit;
}

/* ----------------------------------------
   CLEAR OTP AFTER SUCCESS
---------------------------------------- */
$clear = $conn->prepare(
    "UPDATE users SET otp = NULL, otp_expiry = NULL WHERE email = ?"
);
$clear->bind_param("s", $email);
$clear->execute();

/* ----------------------------------------
   SUCCESS RESPONSE
---------------------------------------- */
echo json_encode([
    "status" => true,
    "message" => "OTP verified successfully"
]);
