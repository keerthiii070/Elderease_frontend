<?php
header("Content-Type: application/json");

$base_url = "http://192.168.31.194/elderease_api/yoga_videos/";

$data = [
    ["id"=>1, "title"=>"Seated Mountain Pose", "video_url"=>$base_url."yoga1.mp4"],
    ["id"=>2, "title"=>"Gentle Neck Rolls", "video_url"=>$base_url."yoga2.mp4"],
    ["id"=>3, "title"=>"Modified Chair Twist", "video_url"=>$base_url."yoga3.mp4"],
    ["id"=>4, "title"=>"Seated Forward Fold", "video_url"=>$base_url."yoga4.mp4"],
    ["id"=>5, "title"=>"Standing Side Reach", "video_url"=>$base_url."yoga5.mp4"],
    ["id"=>6, "title"=>"Heel Raises", "video_url"=>$base_url."yoga6.mp4"],
    ["id"=>7, "title"=>"Knee-to-Chest", "video_url"=>$base_url."yoga7.mp4"],
    ["id"=>8, "title"=>"Seated Arm Circles", "video_url"=>$base_url."yoga8.mp4"],
    ["id"=>9, "title"=>"Standing Weight Shift", "video_url"=>$base_url."yoga9.mp4"],
    ["id"=>10,"title"=>"Reclined Butterfly Rest", "video_url"=>$base_url."yoga10.mp4"]
];

echo json_encode([
    "status" => true,
    "videos" => $data
]);
?>
