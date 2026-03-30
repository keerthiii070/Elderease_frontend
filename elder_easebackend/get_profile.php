<?php
header("Content-Type: application/json");
include "db.php";

/* ----------------------------------------
   READ INPUT (JSON OR FORM-DATA)
---------------------------------------- */
$rawData = file_get_contents("php://input");
$data = json_decode($rawData, true);

// fallback to form-data
if (empty($data)) {
    $data = $_POST;
}

/* ----------------------------------------
   GET EMAIL (PRIMARY KEY)
---------------------------------------- */
$email = $data['email'] ?? '';

if (empty($email)) {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* ----------------------------------------
   FETCH USER PROFILE
---------------------------------------- */
$stmt = $conn->prepare(
    "SELECT 
        id,
        full_name,
        email,
        phone,
        age,
        profile_image,
        points
     FROM users
     WHERE email = ?"
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
   SUCCESS RESPONSE
---------------------------------------- */
echo json_encode([
    "status" => true,
    "profile" => [
        "id" => $user['id'],
        "fullName" => $user['full_name'],
        "email" => $user['email'],
        "phone" => $user['phone'],
        "age" => $user['age'],
        "profileImage" => $user['profile_image'],
        "points" => $user['points']
    ]
]);
?>
