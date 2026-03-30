<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");
include "db.php";

/* ---------------------------
   READ INPUT
---------------------------- */
$data = json_decode(file_get_contents("php://input"), true);

$email      = trim($data['email'] ?? '');
$fullName   = trim($data['full_name'] ?? '');
$age        = (int)($data['age'] ?? 0);
$conditions = trim($data['conditions'] ?? '');
$bloodGroup = trim($data['blood_group'] ?? '');
$weight     = (float)($data['weight_kg'] ?? 0);
$height     = (float)($data['height_cm'] ?? 0);

/* ---------------------------
   VALIDATION
---------------------------- */
if (!$email || !$fullName || $age <= 0 || $weight <= 0 || $height <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid or missing fields"
    ]);
    exit;
}

/* ---------------------------
   BMI CALCULATION
---------------------------- */
$heightMeters = $height / 100;
$bmi = round($weight / ($heightMeters * $heightMeters), 1);

/* ---------------------------
   UPDATE USERS TABLE
---------------------------- */
$stmt = $conn->prepare(
    "UPDATE users SET
        full_name = ?,
        age = ?,
        health_conditions = ?,
        blood_group = ?,
        weight_kg = ?,
        height_cm = ?,
        bmi = ?
     WHERE email = ?"
);

$stmt->bind_param(
    "sissddds",
    $fullName,
    $age,
    $conditions,
    $bloodGroup,
    $weight,
    $height,
    $bmi,
    $email
);

if (!$stmt->execute()) {
    echo json_encode([
        "status" => false,
        "error" => $stmt->error
    ]);
    exit;
}

/* ---------------------------
   RESPONSE
---------------------------- */
echo json_encode([
    "status" => true,
    "message" => "Profile saved successfully",
    "bmi" => $bmi
]);
