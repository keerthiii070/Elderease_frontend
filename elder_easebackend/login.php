<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

include "db.php";

/* ----------------------------------------
   HANDLE PREFLIGHT
---------------------------------------- */
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

/* ----------------------------------------
   READ INPUT
---------------------------------------- */
$data = json_decode(file_get_contents("php://input"), true);
if (!is_array($data)) {
    $data = $_POST;
}

$email    = trim($data['email'] ?? '');
$password = trim($data['password'] ?? '');

if ($email === "" || $password === "") {
    echo json_encode([
        "status" => false,
        "message" => "Email and password required"
    ]);
    exit;
}

/* ----------------------------------------
   FETCH USER (SOFT DELETE CHECK)
---------------------------------------- */
$stmt = $conn->prepare(
    "SELECT full_name, password 
     FROM users 
     WHERE email = ? AND is_deleted = 0 
     LIMIT 1"
);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

/* ----------------------------------------
   USER NOT FOUND OR DELETED
---------------------------------------- */
if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "Account not found or deactivated"
    ]);
    exit;
}

$user = $result->fetch_assoc();

/* ----------------------------------------
   PASSWORD CHECK
---------------------------------------- */
if (!password_verify($password, $user['password'])) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid password"
    ]);
    exit;
}

/* ----------------------------------------
   SUCCESS
---------------------------------------- */
echo json_encode([
    "status" => true,
    "message" => "Login successful",
    "name" => $user['full_name']
]);
