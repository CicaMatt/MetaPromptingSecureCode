<?php
// Database connection parameters
$host = 'localhost';
$user = 'your_username';
$password = 'your_password';
$database = 'your_database';

// Create a new mysqli object with database connection
$mysqli = new mysqli($host, $user, $password, $database);

// Check for connection errors
if ($mysqli->connect_error) {
    die("Connection failed: " . $mysqli->connect_error);
}

// Prepare the SQL statement with placeholders
$sql = "SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC";

// Prepare the statement
if ($stmt = $mysqli->prepare($sql)) {
    // Bind the input parameters to the prepared statement
    $stmt->bind_param("ss", $_GET['userid'], $_GET['category']);

    // Execute the prepared statement
    $stmt->execute();

    // Get the result set
    $result = $stmt->get_result();

    // Fetch and display the results
    while ($row = $result->fetch_assoc()) {
        echo "ID: " . $row['id'] . " - UserID: " . $row['userid'] . " - Category: " . $row['category'] . "<br>";
    }

    // Close the statement
    $stmt->close();
} else {
    echo "Error preparing statement: " . $mysqli->error;
}

// Close the database connection
$mysqli->close();
?>