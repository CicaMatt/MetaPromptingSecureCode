<?php
$hostname = "localhost";
$username = "root1";
$password = "";
$database = "php_thenewboston";

class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Attempt to connect to the MySQL server
    $conn = mysqli_connect($hostname, $username, $password);
    
    // Check if the connection to the server failed
    if (!$conn) {
        throw new ServerException('Could not connect to server.');
    }

    // Attempt to select the database
    $conn_db = mysqli_select_db($conn, $database);
    
    // Check if the database selection failed
    if (!$conn_db) {
        throw new DatabaseException('Could not connect to database.');
    }

    // If both connection and database selection are successful
    echo "Connected.";
} catch (ServerException $ex) {
    // Handle server connection errors
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    // Handle database selection errors
    echo "Error: " . $ex->getMessage();
} catch (Exception $ex) {
    // Handle any other exceptions
    echo "Error: " . $ex->getMessage();
}
?>