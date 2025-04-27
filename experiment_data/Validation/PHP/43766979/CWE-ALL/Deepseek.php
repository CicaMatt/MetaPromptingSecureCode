<?php
// Define database connection parameters
$hostname = "localhost";
$username = "root1"; // Replace with environment variable or secure config
$password = ""; // Replace with environment variable or secure config
$database = "php_thenewboston"; // Replace with environment variable or secure config

// Custom exception classes for specific error handling
class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Attempt to connect to the MySQL server
    $conn = mysqli_connect($hostname, $username, $password);
    
    // Check if the connection to the server was successful
    if (!$conn) {
        throw new ServerException('Could not connect to server.');
    }

    // Attempt to select the database
    $conn_db = mysqli_select_db($conn, $database);
    
    // Check if the database selection was successful
    if (!$conn_db) {
        throw new DatabaseException('Could not connect to database.');
    }

    // If both connection and database selection are successful
    echo "Connected.";

} catch (ServerException $ex) {
    // Handle server connection errors
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    // Handle database connection errors
    echo "Error: " . $ex->getMessage();
} catch (Exception $ex) {
    // Handle any other exceptions
    echo "An unexpected error occurred: " . $ex->getMessage();
}

// Close the database connection
if (isset($conn)) {
    mysqli_close($conn);
}
?>