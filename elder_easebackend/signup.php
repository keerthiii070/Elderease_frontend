<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* ----------------------------------------
   READ INPUT
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) $data = $_POST;

/* ----------------------------------------
   GET FIELDS
---------------------------------------- */
$fullName        = trim($data['fullName'] ?? '');
$email           = trim($data['email'] ?? '');
$phone           = trim($data['phoneNumber'] ?? '');
$age             = trim($data['age'] ?? '');
$password        = trim($data['password'] ?? '');
$confirmPassword = trim($data['confirmPassword'] ?? '');

/* ----------------------------------------
   BASIC VALIDATION
---------------------------------------- */
if (
    $fullName === '' ||
    $email === '' ||
    $phone === '' ||
    $age === '' ||
    $password === '' ||
    $confirmPassword === ''
) {
    echo json_encode(["status" => false, "message" => "All fields are required"]);
    exit;
}

if ($password !== $confirmPassword) {
    echo json_encode(["status" => false, "message" => "Passwords do not match"]);
    exit;
}

if (strlen($password) < 6) {
    echo json_encode(["status" => false, "message" => "Password must be at least 6 characters"]);
    exit;
}

/* ----------------------------------------
   CHECK USER
---------------------------------------- */
$check = $conn->prepare(
    "SELECT id, email_verified, is_deleted 
     FROM users 
     WHERE email = ? LIMIT 1"
);
$check->bind_param("s", $email);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => false, "message" => "Email not found. Verify email first."]);
    exit;
}

$user = $result->fetch_assoc();

if ((int)$user['email_verified'] !== 1) {
    echo json_encode(["status" => false, "message" => "Please verify your email first"]);
    exit;
}

/* ----------------------------------------
   HASH PASSWORD
---------------------------------------- */
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

/* ----------------------------------------
   UPDATE EXISTING USER (ACTIVE OR DELETED)
---------------------------------------- */
$update = $conn->prepare(
    "UPDATE users 
     SET 
        full_name = ?, 
        phone = ?, 
        age = ?, 
        password = ?, 
        is_deleted = 0,
        deleted_at = NULL
     WHERE id = ?"
);

$update->bind_param(
    "ssisi",
    $fullName,
    $phone,
    $age,
    $hashedPassword,
    $user['id']
);

if ($update->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Signup completed successfully",
        "existing_user" => true
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Signup failed"
    ]);
}
