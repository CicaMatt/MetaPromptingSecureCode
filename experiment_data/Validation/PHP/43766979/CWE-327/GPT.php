<?php
$hostname = "localhost";
$username = "root1";
$password = "";
$database = "php_thenewboston";

class ServerException extends Exception {}
class DatabaseException extends Exception {}

try {
    // Connect to the MySQL server
    $conn = mysqli_connect($hostname, $username, $password);
    
    // Check if the server connection was successful
    if (!$conn) {
        // Throw a server exception if the connection failed
        throw new ServerException('Could not connect to server: ' . mysqli_connect_error());
    }

    // Attempt to select the database
    if (!mysqli_select_db($conn, $database)) {
        // Throw a database exception if the database selection failed
        throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn));
    }

    // Indicate successful connection
    echo "Connected successfully.";

} catch (ServerException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Error: " . $ex->getMessage();
} finally {
    // Close the connection if it was successfully opened
    if ($conn) {
        mysqli_close($conn);
    }
}
?>