<?php
$host = "localhost";
$user = "root";        // your DB username
$pass = "";            // your DB password
$db   = "elder_ease";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode([
        "status" => false,
        "message" => "Database connection failed"
    ]));
}
?>
