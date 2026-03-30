<?php
header("Content-Type: application/json");
date_default_timezone_set("Asia/Kolkata");

include "db.php";

/* -------------------------------
   READ INPUT (JSON or FORM-DATA)
-------------------------------- */
$rawInput = file_get_contents("php://input");
$data = json_decode($rawInput, true);

if (!is_array($data)) {
    $data = $_POST;
}

/* -------------------------------
   FETCH FIELDS
-------------------------------- */
$email         = $data['email'] ?? '';
$pain_area     = $data['pain_area'] ?? '';
$pain_points   = $data['pain_points'] ?? [];
$stiffness     = $data['stiffness_minutes'] ?? '';
$pain_severity = isset($data['pain_severity']) ? (int)$data['pain_severity'] : -1;
$pain_level    = $data['pain_level'] ?? '';

/* -------------------------------
   VALIDATION
-------------------------------- */
if (
    empty($email) ||
    empty($pain_area) ||
    empty($stiffness) ||
    $pain_severity < 0 ||
    empty($pain_level)
) {
    echo json_encode([
        "status" => false,
        "message" => "Invalid input data"
    ]);
    exit;
}

/* -------------------------------
   HANDLE pain_points FROM FORM-DATA
-------------------------------- */
if (is_string($pain_points)) {
    $decoded = json_decode($pain_points, true);
    if (json_last_error() === JSON_ERROR_NONE && is_array($decoded)) {
        $pain_points = $decoded;
    } else {
        $pain_points = [];
    }
}

/* -------------------------------
   ENCODE POINTS FOR DB
-------------------------------- */
$pain_points_json = json_encode($pain_points, JSON_UNESCAPED_UNICODE);

/* -------------------------------
   INSERT INTO DATABASE
-------------------------------- */
$sql = "
    INSERT INTO pain_logs
    (email, pain_area, pain_points, stiffness_minutes, pain_severity, pain_level)
    VALUES (?, ?, ?, ?, ?, ?)
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode([
        "status" => false,
        "error" => $conn->error
    ]);
    exit;
}

/*
  TYPES:
  s = email
  s = pain_area
  s = pain_points_json
  s = stiffness_minutes
  i = pain_severity
  s = pain_level
*/
$stmt->bind_param(
    "ssssis",
    $email,
    $pain_area,
    $pain_points_json,
    $stiffness,
    $pain_severity,
    $pain_level
);

/* -------------------------------
   EXECUTE
-------------------------------- */
if (!$stmt->execute()) {
    echo json_encode([
        "status" => false,
        "error" => $stmt->error
    ]);
    exit;
}

/* -------------------------------
   SUCCESS RESPONSE
-------------------------------- */
echo json_encode([
    "status" => true,
    "message" => "Pain log saved successfully",
    "id" => $stmt->insert_id
]);
