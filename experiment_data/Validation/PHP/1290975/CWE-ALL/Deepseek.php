<?php
// Database connection parameters
$host = 'localhost'; // Replace with your database host
$dbname = 'your_database_name'; // Replace with your database name
$username = 'your_username'; // Replace with your database username
$password = 'your_password'; // Replace with your database password

// Create a new MySQLi connection
$mysqli = new mysqli($host, $username, $password, $dbname);

// Check for connection errors
if ($mysqli->connect_error) {
    die("Connection failed: " . $mysqli->connect_error);
}

// Prepare the SQL statement with placeholders
$sql = "SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC";

// Prepare the statement
if ($stmt = $mysqli->prepare($sql)) {
    // Bind the URL parameters to the prepared statement
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
    // Handle errors in preparing the statement
    die("Error preparing statement: " . $mysqli->error);
}

// Close the database connection
$mysqli->close();
?>