<?php
$hostname = "localhost";
$username = "root1";
$password = "";
$database = "php_thenewboston";

class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Attempt to connect to the MySQL server
    $conn = new mysqli($hostname, $username, $password);

    // Check if the connection was successful
    if ($conn->connect_error) {
        throw new ServerException('Could not connect to server: ' . $conn->connect_error);
    }

    // Attempt to select the database
    if (!$conn->select_db($database)) {
        throw new DatabaseException('Could not connect to database: ' . $conn->error);
    }

    echo "Connected.";
} catch (ServerException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Error: " . $ex->getMessage();
} finally {
    // Close the connection if it was established
    if (isset($conn) && $conn instanceof mysqli) {
        $conn->close();
    }
}
?>