<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* 🔥 FORCE MYSQL TIMEZONE (CRITICAL FIX) */
$conn->query("SET time_zone = '+05:30'");

/* ----------------------------------------
   COMPOSER AUTOLOAD
---------------------------------------- */
require __DIR__ . '/vendor/autoload.php';

/* ----------------------------------------
   READ INPUT (ANDROID / POSTMAN / FORM)
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);

if (!is_array($data)) {
    $data = $_POST;
}

$email = trim($data['email'] ?? '');

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* ----------------------------------------
   CHECK EMAIL EXISTS
---------------------------------------- */
$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email not registered"
    ]);
    exit;
}

$user = $result->fetch_assoc();

/* ----------------------------------------
   GENERATE OTP
---------------------------------------- */
$otp = random_int(100000, 999999);
$expiry = date("Y-m-d H:i:s", strtotime("+5 minutes"));

$update = $conn->prepare(
    "UPDATE users SET otp = ?, otp_expiry = ? WHERE id = ?"
);
$update->bind_param("ssi", $otp, $expiry, $user['id']);
$update->execute();

/* ----------------------------------------
   SEND OTP EMAIL
---------------------------------------- */
$mail = new PHPMailer(true);

try {
    // SMTP CONFIG
    $mail->isSMTP();
    $mail->Host       = "smtp.gmail.com";
    $mail->SMTPAuth   = true;
    $mail->Username   = "elderrease@gmail.com"; // ✅ YOUR GMAIL
    $mail->Password   = "czxj odaq maoe knzn"; // ✅ APP PASSWORD
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    // EMAIL HEADERS
    $mail->setFrom("elderrease@gmail.com", "ElderEase Support");
    $mail->addAddress($email);

    // EMAIL CONTENT
    $mail->isHTML(true);
    $mail->Subject = "ElderEase - Password Reset OTP";

    $mail->Body = "
        <div style='font-family: Arial, sans-serif'>
            <h2>Password Reset Request</h2>
            <p>Your OTP is:</p>
            <h1 style='letter-spacing: 4px;'>$otp</h1>
            <p>This OTP is valid for <b>5 minutes</b>.</p>
            <p>If you did not request this, please ignore.</p>
            <br>
            <p>— ElderEase Team</p>
        </div>
    ";

    $mail->AltBody = "Your OTP is $otp. Valid for 5 minutes.";

    $mail->send();

    echo json_encode([
        "status" => true,
        "message" => "OTP sent to your email"
    ]);

} catch (Exception $e) {
    echo json_encode([
        "status" => false,
        "message" => "Email failed: " . $mail->ErrorInfo
    ]);
}
