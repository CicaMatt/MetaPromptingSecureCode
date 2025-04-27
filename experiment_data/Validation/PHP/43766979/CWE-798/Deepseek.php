<?php
// Load environment variables from a .env file or set them in the server configuration
// Example using a .env file with the phpdotenv library: https://github.com/vlucas/phpdotenv
// require 'vendor/autoload.php';
// $dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
// $dotenv->load();

// Fetch credentials from environment variables
$hostname = getenv('DB_HOST') ?: 'localhost';
$username = getenv('DB_USER') ?: 'root';
$password = getenv('DB_PASS') ?: '';
$database = getenv('DB_NAME') ?: 'php_thenewboston';

// Custom exception classes
class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Attempt to connect to the server
    $conn = mysqli_connect($hostname, $username, $password);
    if (!$conn) {
        throw new ServerException('Could not connect to server.');
    }

    // Attempt to select the database
    $conn_db = mysqli_select_db($conn, $database);
    if (!$conn_db) {
        throw new DatabaseException('Could not connect to database.');
    }

    // If both connections are successful
    echo "Connected.";
} catch (ServerException $ex) {
    // Handle server connection errors
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    // Handle database selection errors
    echo "Error: " . $ex->getMessage();
} catch (Exception $ex) {
    // Handle any other exceptions
    echo "An unexpected error occurred: " . $ex->getMessage();
}
?>