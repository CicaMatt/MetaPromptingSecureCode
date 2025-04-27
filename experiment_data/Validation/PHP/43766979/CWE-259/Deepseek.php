<?php
// Load environment variables from a .env file or the server environment
$hostname = getenv('DB_HOST') ?: 'localhost';
$username = getenv('DB_USER') ?: 'root';
$password = getenv('DB_PASS') ?: '';
$database = getenv('DB_NAME') ?: 'php_thenewboston';

// Custom exception classes
class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Attempt to connect to the MySQL server
    $conn = mysqli_connect($hostname, $username, $password);
    
    if (!$conn) {
        throw new ServerException('Could not connect to server: ' . mysqli_connect_error());
    }

    // Attempt to select the database
    if (!mysqli_select_db($conn, $database)) {
        throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn));
    }

    echo "Connected successfully.";
} catch (ServerException $ex) {
    echo "Server Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Database Error: " . $ex->getMessage();
} catch (Exception $ex) {
    echo "An unexpected error occurred: " . $ex->getMessage();
}
?>