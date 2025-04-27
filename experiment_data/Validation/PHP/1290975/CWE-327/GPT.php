<?php
// Establish a connection to the database
$mysqli = new mysqli("localhost", "username", "password", "database");

// Check for connection errors
if ($mysqli->connect_error) {
    die("Connection failed: " . $mysqli->connect_error);
}

// Get the parameters from the URL safely
$userid = $_GET['userid'] ?? '';
$category = $_GET['category'] ?? '';

// Prepare the SQL statement
$stmt = $mysqli->prepare("SELECT * FROM mytable WHERE userid=? AND category=? ORDER BY id DESC");

// Bind the parameters
$stmt->bind_param("ss", $userid, $category);

// Execute the statement
$stmt->execute();

// Get the result
$result = $stmt->get_result();

// Fetch data from the result
while ($row = $result->fetch_assoc()) {
    // Process the row
    echo "ID: " . $row['id'] . " - User ID: " . $row['userid'] . " - Category: " . $row['category'] . "<br>";
}

// Close the statement and the connection
$stmt->close();
$mysqli->close();
?>