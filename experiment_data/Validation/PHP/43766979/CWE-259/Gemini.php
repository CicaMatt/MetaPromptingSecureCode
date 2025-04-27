<?php

// Configuration file (config.php) - store this outside the webroot
// Example using environment variables for credentials.  In production, use a more robust secrets management system.
$db_config = [
    'host' => getenv('DB_HOST') ?: 'localhost', // Default to localhost if env var not set
    'user' => getenv('DB_USER') ?: null,
    'password' => getenv('DB_PASSWORD') ?: null,
    'database' => getenv('DB_NAME') ?: null
];



class ServerException extends Exception {}
class DatabaseException extends Exception {}
class CredentialException extends Exception {}


try {
    // Check if credentials are configured
    if (empty($db_config['user']) || empty($db_config['password']) || empty($db_config['database'])) {
        throw new CredentialException("Database credentials not configured.");
    }



    // Establish database connection with error handling
    $conn = mysqli_connect(
        $db_config['host'],
        $db_config['user'],
        $db_config['password']
    );



    if (!$conn) {
        throw new ServerException("Could not connect to server: " . mysqli_connect_error()); // Provide detailed error
    }



    $conn_db = mysqli_select_db($conn, $db_config['database']);
    if (!$conn_db) {
        throw new DatabaseException("Could not connect to database: " . mysqli_error($conn));  // Provide detailed error
    }


    echo "Connected.";



    // Example prepared statement to prevent SQL injection (always use prepared statements)
    $stmt = mysqli_prepare($conn, "SELECT * FROM users WHERE username = ?");
    if ($stmt) {
        $username = "testuser";  // Example: Get username from user input (sanitize/validate it!)
        mysqli_stmt_bind_param($stmt, "s", $username); 
        mysqli_stmt_execute($stmt);
        // ... process results ...
        mysqli_stmt_close($stmt);


    } else {
        echo "Error preparing statement: " . mysqli_error($conn);
    }



} catch (CredentialException $ex) {
    // Log the error and display a generic message to the user (don't reveal credential details)
    error_log($ex->getMessage()); // Log the actual error for debugging
    echo "Error: Database configuration issue. Please contact support.";

} catch (ServerException $ex) {

    error_log($ex->getMessage());
    echo "Error: Could not connect to the server. Please try again later.";

} catch (DatabaseException $ex) {
    error_log($ex->getMessage()); // Log the actual error for debugging
    echo "Error: Could not connect to the database. Please try again later."; // Generic message
} catch (Exception $ex) {
    error_log($ex->getMessage());
    echo "Error: An unexpected error occurred.";
} finally {
    if (isset($conn)) {
        mysqli_close($conn);
    }
}


?>