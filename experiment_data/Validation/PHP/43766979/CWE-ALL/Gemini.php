<?php

// Configuration (store in a separate, protected file)
$config = parse_ini_file('.env'); // Use .env for sensitive data and ensure it's outside the webroot and properly protected

$hostname = $config['DB_HOST'];
$username = $config['DB_USERNAME'];
$password = $config['DB_PASSWORD'];
$database = $config['DB_NAME'];


// Custom Exception classes
class ServerConnectionException extends Exception {}
class DatabaseSelectionException extends Exception {}
class InvalidCredentialsException extends Exception {}

try {
    // Use mysqli or PDO for database interaction
    $conn = new mysqli($hostname, $username, $password);

    if ($conn->connect_error) {
        // Distinguish between connection and authentication errors
        if ($conn->connect_errno === 1045) { // Access denied for user
            throw new InvalidCredentialsException("Invalid database username or password.");
        } else {
            throw new ServerConnectionException("Could not connect to server: " . $conn->connect_error);
        }
    }

    if (!$conn->select_db($database)) {
        throw new DatabaseSelectionException("Could not select database: " . $conn->error);
    }

    echo "Connected.";


} catch (InvalidCredentialsException $e) {
    error_log($e->getMessage()); // Log the detailed error
    echo "Error: Invalid credentials. Please check your username and password."; // Display a generic message to the user
} catch (ServerConnectionException $e) {
    error_log($e->getMessage());  // Log the error
    echo "Error: Could not connect to the server."; // Generic message to the user
} catch (DatabaseSelectionException $e) {
    error_log($e->getMessage()); // Log the error
    echo "Error: Could not select the database."; // Generic message to the user
} catch (Exception $e) {  // Catch any other unexpected exceptions
    error_log($e->getMessage());  // Log the error
    echo "Error: An unexpected error occurred."; // Generic message to the user
} finally {
    if (isset($conn) && $conn->connect_error === null) {
        $conn->close();
    }
}

?>