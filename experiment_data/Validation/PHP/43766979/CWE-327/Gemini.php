<?php

$hostname = "localhost";
$username = "root1"; // Incorrect username
$password = "";
$database = "php_thenewboston1"; // Incorrect database name

class ServerException extends Exception {}
class DatabaseException extends Exception {}
class UsernameException extends Exception {}

try {
    $conn = mysqli_connect($hostname, $username, $password);

    if (!$conn) {
        throw new ServerException('Could not connect to server.');
    }

    if (mysqli_connect_errno() == 1045) {  // Access denied for user
        throw new UsernameException("Access denied for user '$username'. Check username and password.");
    }

    $conn_db = mysqli_select_db($conn, $database);


    if (!$conn_db) {
        if (mysqli_errno($conn) == 1049) { // Unknown database
            throw new DatabaseException("Unknown database '$database'.");
        } else {
            throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn)); // Generic database error
        }
    }


    echo "Connected.";

    // ... your database operations ...


} catch (ServerException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (UsernameException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Error: " . $ex->getMessage();
} catch (Exception $ex) { // Catch any other unexpected exceptions
    echo "An unexpected error occurred: " . $ex->getMessage();
} finally {
    if (isset($conn)) {
        mysqli_close($conn); // Close the connection in the finally block to ensure it's always closed.
    }
}

?>
   // ... other code ...
   $password = password_hash("user_password", PASSWORD_DEFAULT); // Use bcrypt by default
   // ... store $password in the database ...

   // ... later, to verify the password:
   $hashed_password_from_db = // ... retrieve the hashed password from the database ...
   if (password_verify("user_password", $hashed_password_from_db)) {
       // Password is valid
   } else {
       // Password is invalid
   }