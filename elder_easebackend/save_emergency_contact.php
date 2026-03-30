<?php
header("Content-Type: application/json");
include "db.php";

/* ---------- READ INPUT (JSON + FORM SAFE) ---------- */
$raw = file_get_contents("php://input");
$data = json_decode($raw, true);

$email       = trim($data['user_email'] ?? $_POST['user_email'] ?? '');
$name        = trim($data['contact_name'] ?? $_POST['contact_name'] ?? '');
$relation    = trim($data['relationship'] ?? $_POST['relationship'] ?? '');
$contactMail = trim($data['contact_email'] ?? $_POST['contact_email'] ?? '');
$phone       = trim($data['contact_phone'] ?? $_POST['contact_phone'] ?? '');
$age         = (int)($data['contact_age'] ?? $_POST['contact_age'] ?? 0);

/* ---------- VALIDATION ---------- */
if (!$email || !$name || !$phone) {
    echo json_encode([
        "status" => false,
        "message" => "Required fields missing"
    ]);
    exit;
}

/* ---------- UPSERT (INSERT OR UPDATE) ---------- */
$stmt = $conn->prepare("
    INSERT INTO emergency_contacts
    (user_email, contact_name, relationship, contact_email, contact_phone, contact_age)
    VALUES (?, ?, ?, ?, ?, ?)
    ON DUPLICATE KEY UPDATE
        contact_name = VALUES(contact_name),
        relationship = VALUES(relationship),
        contact_email = VALUES(contact_email),
        contact_phone = VALUES(contact_phone),
        contact_age = VALUES(contact_age)
");

$stmt->bind_param(
    "sssssi",
    $email,
    $name,
    $relation,
    $contactMail,
    $phone,
    $age
);

if ($stmt->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Emergency contact saved successfully"
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Database error",
        "error" => $stmt->error
    ]);
}
