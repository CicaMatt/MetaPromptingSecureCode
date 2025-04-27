<?php
// Note: For this example, assume the credentials are securely stored in an external configuration
// file and retrieved here for demonstration purposes.

// Load credentials and sensitive information from a secure location or environment variables
$hostname = getenv('DB_HOST');
$username = getenv('DB_USER');
$password = getenv('DB_PASS');
$database = getenv('DB_NAME');

// Custom exception classes for better error handling
class ServerException extends Exception {}  
class DatabaseException extends Exception {}

try {
    // Attempt to connect to the MySQL server
    $conn = mysqli_connect($hostname, $username, $password);
    
    if (!$conn) {
        // Differentiate between server and authentication errors
        if (mysqli_connect_errno() === 1045) {
            // Error number 1045 indicates access denied for user (bad username/password)
            throw new ServerException('Invalid username/password. Could not connect to server.');
        } else {
            throw new ServerException('Could not connect to server. Error: ' . mysqli_connect_error());
        }
    }

    // Select the database
    $conn_db = mysqli_select_db($conn, $database);

    if (!$conn_db) {
        // Database selection error handling
        throw new DatabaseException('Could not connect to database. Please check the database name.');
    }

    echo "Connected.";

} catch (ServerException $ex) {
    // Specific error message for server connection issues
    error_log('Server connection error: ' . $ex->getMessage()); // Log error for auditing
    echo "Error: " . $ex->getMessage();        
} catch (DatabaseException $ex) {
    // Specific error message for database selection issues
    error_log('Database connection error: ' . $ex->getMessage()); // Log error for auditing
    echo "Error: " . $ex->getMessage();
}

// Close the connection if successful
if ($conn) {
    mysqli_close($conn);
}
?>