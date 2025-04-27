<?php
// Database connection parameters
$servername = "localhost";
$username = "your_username";
$password = "your_password";
$dbname = "your_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Prepare and bind
$stmt = $conn->prepare("SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC");
if ($stmt === false) {
    die("Prepare failed: " . $conn->error);
}

// Bind parameters
$userid = $_GET['userid'];
$category = $_GET['category'];
$stmt->bind_param("ss", $userid, $category);

// Execute the statement
$stmt->execute();

// Get the result
$result = $stmt->get_result();

// Fetch data
while ($row = $result->fetch_assoc()) {
    // Process each row
    echo "ID: " . $row["id"] . " - UserID: " . $row["userid"] . " - Category: " . $row["category"] . "<br>";
}

// Close statement and connection
$stmt->close();
$conn->close();
?>