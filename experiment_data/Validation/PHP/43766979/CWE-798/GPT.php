<?php
// Retrieve credentials from a secure location
$hostname = getenv('DB_HOSTNAME');
$username = getenv('DB_USERNAME');
$password = getenv('DB_PASSWORD');
$database = getenv('DB_DATABASE');

// Function to connect to the database
function connectToDatabase($hostname, $username, $password, $database) {
    $conn = mysqli_connect($hostname, $username, $password);

    if (!$conn) {
        throw new ServerException('Could not connect to server: ' . mysqli_connect_error());
    }

    if (!mysqli_select_db($conn, $database)) {
        throw new DatabaseException('Could not select database: ' . mysqli_error($conn));
    }

    return $conn;
}

class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    $conn = connectToDatabase($hostname, $username, $password, $database);
    echo "Connected successfully.";
} catch (ServerException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Error: " . $ex->getMessage();
}