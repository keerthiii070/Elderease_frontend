<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

ini_set('display_errors', 1);
error_reporting(E_ALL);

/* READ INPUT */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) $data = $_POST;

$email = trim($data['email'] ?? '');

/* VALIDATE EMAIL */
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid email address"
    ]);
    exit;
}

/* CHECK IF EMAIL EXISTS */
$check = $conn->prepare(
    "SELECT id, email_verified FROM users WHERE email = ? LIMIT 1"
);
$check->bind_param("s", $email);
$check->execute();
$result = $check->get_result();

/* EMAIL EXISTS → ALLOW OTP RESEND */
if ($result->num_rows > 0) {
    echo json_encode([
        "status" => true,
        "message" => "Email already exists"
    ]);
    exit;
}

/* INSERT EMAIL ONLY */
$insert = $conn->prepare(
    "INSERT INTO users (email, email_verified, is_deleted)
     VALUES (?, 0, 0)"
);

$insert->bind_param("s", $email);

if ($insert->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Email reserved successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Pre-signup failed",
        "error" => $conn->error
    ]);
}
