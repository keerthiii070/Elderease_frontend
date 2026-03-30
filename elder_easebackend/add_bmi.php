<?php
date_default_timezone_set("Asia/Kolkata");
header("Content-Type: application/json");
include "db.php";

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) $data = $_POST;

$email     = $data['email'] ?? '';
$gender    = $data['gender'] ?? '';
$age       = (int)($data['age'] ?? 0);
$heightCm  = (float)($data['height_cm'] ?? 0);
$weightKg  = (int)($data['weight_kg'] ?? 0);

if (!$email || !$gender || $age <= 0 || $heightCm <= 0 || $weightKg <= 0) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid input"
    ]);
    exit;
}

$heightM = $heightCm / 100;
$bmi = round($weightKg / ($heightM * $heightM), 1);

$category = match (true) {
    $bmi < 18.5 => "Underweight",
    $bmi < 25 => "Normal",
    $bmi < 30 => "Overweight",
    default => "Obese"
};

$stmt = $conn->prepare(
    "INSERT INTO bmi_records 
    (email, gender, age, height_cm, weight_kg, bmi, category)
    VALUES (?, ?, ?, ?, ?, ?, ?)"
);
$stmt->bind_param(
    "ssiidis",
    $email,
    $gender,
    $age,
    $heightCm,
    $weightKg,
    $bmi,
    $category
);
$stmt->execute();

echo json_encode([
    "status" => true,
    "bmi" => $bmi,
    "category" => $category,
    "age" => $age
]);
