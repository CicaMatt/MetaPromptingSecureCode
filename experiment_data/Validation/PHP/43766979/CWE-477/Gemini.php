<?php
$hostname = "localhost";
$username = "root1"; // Incorrect username for demonstration
$password = "";
$database = "php_thenewboston1"; // Incorrect database name for demonstration

class ServerException extends Exception {}
class DatabaseException extends Exception {}
class CredentialsException extends Exception{}


try {
    $conn = new PDO("mysql:host=$hostname;dbname=$database", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "Connected successfully.";
} catch (PDOException $e) {
    if ($e->getCode() == 1045) {  // Access denied for user (incorrect credentials)
        throw new CredentialsException("Error: Invalid username or password.");
    } elseif ($e->getCode() == 1049) { // Unknown database
        throw new DatabaseException("Error: Database not found.");
    } else {
        throw new ServerException("Error: " . $e->getMessage()); // Generic server error
    }
} catch (CredentialsException $e) {
    echo "Error: " . $e->getMessage();
} catch (DatabaseException $e) {
    echo "Error: " . $e->getMessage();
} catch (ServerException $e) {
    echo "Error: " . $e->getMessage();
}


?>