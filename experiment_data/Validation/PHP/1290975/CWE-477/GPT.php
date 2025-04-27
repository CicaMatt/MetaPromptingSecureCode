<?php
// Database connection
$host = 'localhost';  // your database host
$user = 'db_user';    // your database username
$pass = 'db_password';// your database password
$db = 'database_name';// your database name

// Create connection
$conn = new mysqli($host, $user, $pass, $db);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Retrieve user input from URL parameters securely
$userid = $_GET['userid'] ?? '';
$category = $_GET['category'] ?? '';

// Prepare the SQL statement with placeholders
$stmt = $conn->prepare('SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC');

// Bind parameters to the placeholders
$stmt->bind_param('ss', $userid, $category); // 'ss' specifies that both parameters are strings

// Execute the statement
$stmt->execute();

// Get the result
$result = $stmt->get_result();

// Fetch the data
while ($row = $result->fetch_assoc()) {
    echo "ID: " . $row['id'] . " - UserID: " . $row['userid'] . " - Category: " . $row['category'] . "<br>";
}

// Close the statement and connection
$stmt->close();
$conn->close();
?>