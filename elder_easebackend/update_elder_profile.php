<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");
include "db.php";

/* -----------------------------
   READ INPUT
------------------------------ */
$email        = trim($_POST['email'] ?? '');
$full_name    = trim($_POST['full_name'] ?? '');
$age          = $_POST['age'] ?? null;
$weight_kg    = $_POST['weight_kg'] ?? null;
$blood_group  = trim($_POST['blood_group'] ?? '');
$conditions   = trim($_POST['health_conditions'] ?? '');
$phone        = trim($_POST['phone'] ?? '');

/* -----------------------------
   VALIDATION
------------------------------ */
if ($email === '') {
    echo json_encode([
        "status" => false,
        "message" => "Email is required"
    ]);
    exit;
}

/* -----------------------------
   CHECK USER EXISTS
------------------------------ */
$check = $conn->prepare("SELECT id, profile_image FROM users WHERE email = ? LIMIT 1");
$check->bind_param("s", $email);
$check->execute();
$result = $check->get_result();

if ($result->num_rows === 0) {
    echo json_encode([
        "status" => false,
        "message" => "User not found"
    ]);
    exit;
}

$user = $result->fetch_assoc();

/* -----------------------------
   IMAGE UPLOAD (SAVE PROPERLY)
------------------------------ */
$profile_img = ""; // default = no new image

if (isset($_FILES['profile_image']) && $_FILES['profile_image']['error'] === 0) {

    $uploadDir = "uploads/profile_images/";

    // Create folder if not exists
    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0777, true);
    }

    // Get file extension
    $ext = pathinfo($_FILES['profile_image']['name'], PATHINFO_EXTENSION);

    // Create unique file name
    $newName = "profile_" . time() . "_" . rand(1000, 9999) . "." . $ext;

    $targetPath = $uploadDir . $newName;

    // Move file to folder
    if (move_uploaded_file($_FILES['profile_image']['tmp_name'], $targetPath)) {
        $profile_img = $newName;
    } else {
        echo json_encode([
            "status" => false,
            "message" => "Image upload failed"
        ]);
        exit;
    }
}

/* -----------------------------
   UPDATE USER
------------------------------ */
$update = $conn->prepare("
    UPDATE users SET
        full_name = COALESCE(NULLIF(?, ''), full_name),
        age = COALESCE(?, age),
        weight_kg = COALESCE(?, weight_kg),
        blood_group = COALESCE(NULLIF(?, ''), blood_group),
        health_conditions = COALESCE(NULLIF(?, ''), health_conditions),
        phone = COALESCE(NULLIF(?, ''), phone),
        profile_image = COALESCE(NULLIF(?, ''), profile_image)
    WHERE email = ?
");

$update->bind_param(
    "siisssss",
    $full_name,
    $age,
    $weight_kg,
    $blood_group,
    $conditions,
    $phone,
    $profile_img,
    $email
);

if ($update->execute()) {
    echo json_encode([
        "status" => true,
        "message" => "Profile updated successfully",
        "rows" => $update->affected_rows,
        "new_image" => $profile_img
    ]);
} else {
    echo json_encode([
        "status" => false,
        "message" => "Update failed",
        "error" => $conn->error
    ]);
}

exit;
?>
