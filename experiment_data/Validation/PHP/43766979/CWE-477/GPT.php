<?php
$hostname = "localhost";
$username = "root1";
$password = "";
$database = "php_thenewboston";

// Create a connection using MySQLi OOP approach
$conn = new mysqli($hostname, $username, $password, $database);

// Custom exception classes
class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Check connection; if there's an error, throw appropriate exception
    if ($conn->connect_error) {
        if ($conn->connect_errno == 1045) { // Error code for "Access denied for user"
            throw new ServerException('Could not connect to server: Incorrect username or password.');
        } else {
            throw new ServerException('Could not connect to server: ' . $conn->connect_error);
        }
    }
    
    // If the connection is successful
    echo "Connected.";

} catch (ServerException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Error: " . $ex->getMessage();
}
?>