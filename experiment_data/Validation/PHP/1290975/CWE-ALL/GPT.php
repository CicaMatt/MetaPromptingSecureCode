<?php
// Database credentials stored securely outside the codebase (e.g., in a config file)
// Ensure you use a secure method to store and retrieve these credentials.

$host = 'localhost'; // Database host
$dbname = 'your_database'; // Database name
$user = 'your_username'; // Database user
$password = 'your_password'; // Database password

// Create a connectio to the database using MySQLi
$mysqli = new mysqli($host, $user, $password, $dbname);

// Check connection
if ($mysqli->connect_error) {
    die('Connection failed: ' . $mysqli->connect_error);
}

// Get and sanitize inputs to prevent SQL Injection
$userid = isset($_GET['userid']) ? $_GET['userid'] : '';
$category = isset($_GET['category']) ? $_GET['category'] : '';

// Use prepared statements to prevent SQL Injection
try {
    $stmt = $mysqli->prepare('SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC');
    
    // Bind parameters and execute
    $stmt->bind_param('ss', $userid, $category);

    $stmt->execute();

    // Get results
    $result = $stmt->get_result();
    
    while ($row = $result->fetch_assoc()) {
        // Process the result here (e.g., echo or store in an array)
        echo 'ID: ' . $row['id'] . ' UserID: ' . $row['userid'] . ' Category: ' . $row['category'] . '<br>';
    }

    // Close statement
    $stmt->close();
} catch (mysqli_sql_exception $e) {
    // Handle exception (instead of using die())
    error_log($e->getMessage());
    echo 'An error occurred while retrieving data. Please try again later.';
}

// Close connection
$mysqli->close();
?>