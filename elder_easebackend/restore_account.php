<?php
header("Content-Type: application/json");
include "db.php";

/* ----------------------------------------
   READ INPUT (JSON OR FORM DATA)
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) {
    $data = $_POST;
}

$email    = trim($data['email'] ?? '');
$password = trim($data['password'] ?? '');

/* ----------------------------------------
   VALIDATION
---------------------------------------- */
if ($email === '' || $password === '') {
    echo json_encode([
        "status" => false,
        "message" => "Email and password are required"
    ]);
    exit;
}

/* ----------------------------------------
   FETCH USER (ONLY SOFT-DELETED)
---------------------------------------- */
$stmt = $conn->prepare(
    "SELECT id, password, is_deleted 
     FROM users 
     WHERE email = ? 
     LIMIT 1"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Account not found"
    ]);
    exit;
}

$user = $result->fetch_assoc();

/* ----------------------------------------
   CHECK IF ACCOUNT IS DELETED
---------------------------------------- */
if ((int)$user['is_deleted'] !== 1) {
    echo json_encode([
        "status" => false,
        "message" => "Account is already active"
    ]);
    exit;
}

/* ----------------------------------------
   VERIFY PASSWORD
---------------------------------------- */
if (!password_verify($password, $user['password'])) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid password"
    ]);
    exit;
}

/* ----------------------------------------
   RESTORE ACCOUNT
---------------------------------------- */
$restore = $conn->prepare(
    "UPDATE users 
     SET is_deleted = 0, deleted_at = NULL 
     WHERE id = ?"
);
$restore->bind_param("i", $user['id']);
$restore->execute();

/* ----------------------------------------
   RESPONSE
---------------------------------------- */
echo json_encode([
    "status" => true,
    "message" => "Account restored successfully"
]);
