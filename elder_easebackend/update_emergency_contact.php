<?php
header("Content-Type: application/json");
include "db.php";

/* -----------------------------
   READ INPUT
------------------------------ */
$user_email    = trim($_POST["email"] ?? "");  // coming from Android as "email"
$contact_name  = trim($_POST["contact_name"] ?? "");
$relationship  = trim($_POST["relationship"] ?? "");
$contact_phone = trim($_POST["contact_phone"] ?? "");
$contact_email = trim($_POST["contact_email"] ?? "");
$contact_age   = intval($_POST["contact_age"] ?? 0);

/* -----------------------------
   VALIDATION
------------------------------ */
if ($user_email === "") {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* -----------------------------
   UPSERT INTO emergency_contacts
------------------------------ */
$sql = "INSERT INTO emergency_contacts
            (user_email, contact_name, relationship, contact_email, contact_phone, contact_age)
        VALUES (?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            contact_name = VALUES(contact_name),
            relationship = VALUES(relationship),
            contact_email = VALUES(contact_email),
            contact_phone = VALUES(contact_phone),
            contact_age = VALUES(contact_age)";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "message" => "Prepare failed",
        "error" => $conn->error
    ]);
    exit;
}

$stmt->bind_param(
    "sssssi",
    $user_email,
    $contact_name,
    $relationship,
    $contact_email,
    $contact_phone,
    $contact_age
);

if ($stmt->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Emergency contact updated successfully",
        "rows" => $stmt->affected_rows
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Emergency update failed",
        "error" => $conn->error
    ]);
}

exit;
?>
