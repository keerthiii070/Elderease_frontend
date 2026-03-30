<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";
require __DIR__ . '/vendor/autoload.php';

/* ----------------------------------------
   READ INPUT (JSON OR FORM DATA)
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) {
    $data = $_POST;
}

$email = trim($data['email'] ?? '');

/* ----------------------------------------
   EMAIL VALIDATION
---------------------------------------- */
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid email address"
    ]);
    exit;
}

/* ----------------------------------------
   CHECK EMAIL RESERVED (PRE-SIGNUP)
---------------------------------------- */
$check = $conn->prepare(
    "SELECT id, email_verified 
     FROM users 
     WHERE email = ? 
     LIMIT 1"
);
$check->bind_param("s", $email);
$check->execute();
$result = $check->get_result();

/* ❌ EMAIL NOT RESERVED */
if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email not reserved. Please start signup again."
    ]);
    exit;
}

$user = $result->fetch_assoc();

/* ❌ EMAIL ALREADY VERIFIED */
if ((int)$user['email_verified'] === 1) {
    echo json_encode([
        "status" => false,
        "message" => "Email already verified"
    ]);
    exit;
}

/* ----------------------------------------
   GENERATE OTP
---------------------------------------- */
$otp = random_int(100000, 999999);
$expiry = date("Y-m-d H:i:s", strtotime("+5 minutes"));

/* ----------------------------------------
   SAVE OTP
---------------------------------------- */
$update = $conn->prepare(
    "UPDATE users 
     SET email_otp = ?, email_otp_expiry = ?
     WHERE email = ?"
);
$update->bind_param("sss", $otp, $expiry, $email);

if (!$update->execute()) {
    echo json_encode([
        "status" => false,
        "message" => "Failed to generate OTP"
    ]);
    exit;
}

/* ----------------------------------------
   SEND EMAIL USING PHPMailer
---------------------------------------- */
$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host       = "smtp.gmail.com";
    $mail->SMTPAuth   = true;
    $mail->Username   = "elderrease@gmail.com";
    $mail->Password   = "czxj odaq maoe knzn"; // 🔴 App password here
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    $mail->setFrom("elderrease@gmail.com", "ElderEase");
    $mail->addAddress($email);

    $mail->isHTML(true);
    $mail->Subject = "Email Verification OTP";
    $mail->Body = "
        <h2>Email Verification</h2>
        <h1>$otp</h1>
        <p>This OTP is valid for 5 minutes.</p>
    ";

    $mail->send();

    echo json_encode([
        "status" => true,
        "message" => "OTP sent to email"
    ]);

} catch (Exception $e) {
    echo json_encode([
        "status" => false,
        "message" => "Email sending failed",
        "error" => $mail->ErrorInfo
    ]);
}
