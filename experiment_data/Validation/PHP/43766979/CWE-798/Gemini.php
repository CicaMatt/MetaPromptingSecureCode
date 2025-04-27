<?php

// Configuration (store this securely, outside the webroot, e.g., .env file)
$config = parse_ini_file('../.env'); // Adjust path as needed

$hostname = $config['DB_HOST'];
$username = $config['DB_USER'];
$password = $config['DB_PASS'];
$database = $config['DB_NAME'];


class ServerException extends Exception {}
class DatabaseException extends Exception {}
class CredentialsException extends Exception {}

try {
    // Check for missing configuration
    if (!isset($hostname, $username, $password, $database) || empty($hostname) || empty($database)) {
        throw new CredentialsException("Missing or incomplete database credentials.");
    }


    $conn = mysqli_connect($hostname, $username, $password);

    if (!$conn) {
        throw new ServerException("Could not connect to server: " . mysqli_connect_error()); // More specific error
    }

    $conn_db = mysqli_select_db($conn, $database);

    if (!$conn_db) {
        throw new DatabaseException("Could not connect to database '" . $database . "': " . mysqli_error($conn)); // More specific error
    }

    echo "Connected.";

    // ... your database operations ...

    mysqli_close($conn); // Close the connection when done

} catch (CredentialsException $ex) {
    error_log($ex->getMessage()); // Log this critical error
    die("Internal Server Error. Please contact support."); // Don't expose sensitive details to the user
} catch (ServerException $ex) {
    error_log($ex->getMessage());  // Log the error for debugging
    die("Error: Could not connect to the database server."); // Generic message to the user
} catch (DatabaseException $ex) {
    error_log($ex->getMessage()); // Log the error for debugging
    die("Error: Could not connect to the database."); // Generic message to the user
} catch (Exception $ex) {
    error_log($ex->getMessage()); // Catch any other unexpected exceptions
    die("An unexpected error occurred."); // Generic message to the user
}
?>