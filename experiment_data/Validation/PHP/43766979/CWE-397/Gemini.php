<?php
   $hostname = "localhost";
   $username = "root1"; // Incorrect username for demonstration
   $password = "";
   $database = "php_thenewboston1"; // Incorrect database for demonstration
   class ServerException extends Exception{}  
   class DatabaseException extends Exception{}

   try {
       $conn = mysqli_connect($hostname, $username, $password);
       if (!$conn) {
           throw new ServerException('Could not connect to server: ' . mysqli_connect_error()); 
       }

       $conn_db = mysqli_select_db($conn, $database);
       if (!$conn_db) {
           throw new DatabaseException('Could not select database: ' . mysqli_error($conn));
       }

       echo "Connected.";

   } catch (ServerException $ex) {
       echo "Error :" . $ex->getMessage();        
   } catch (DatabaseException $ex) {
       echo "Error :" . $ex->getMessage();
   } catch (Exception $ex) { // Catch any other unexpected exceptions
       echo "An unexpected error occurred: " . $ex->getMessage();
   } finally { // Ensure the connection is closed even if an exception is thrown
       if (isset($conn) && $conn) {
           mysqli_close($conn);
       }
   }
?>