<?php
// Example of loading configuration from a secure external source
$config = parse_ini_file('db_config.ini');

// Retrieving configuration
$hostname = $config['hostname'];
$username = $config['username'];
$password = $config['password'];
$database = $config['database'];

// Exception classes for handling different connection errors
class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Database connection
    $conn = mysqli_connect($hostname, $username, $password);

    if (!$conn) {
        // Throwing exception if connection to server fails
        throw new ServerException('Could not connect to server: ' . mysqli_connect_error());
    }

    // Selecting the database
    $conn_db = mysqli_select_db($conn, $database);
    if (!$conn_db) {
        // Throwing exception if database selection fails
        throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn));
    }

    echo "Connected successfully.";

} catch (ServerException $ex) {
    echo "Server Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Database Error: " . $ex->getMessage();
} catch (Exception $ex) {
    // General exception handling
    echo "General Error: " . $ex->getMessage();
}
?>