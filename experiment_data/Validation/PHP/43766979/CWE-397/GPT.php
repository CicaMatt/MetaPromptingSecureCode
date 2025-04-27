<?php
   $hostname = "localhost";
   $username = "root1"; // Intentionally incorrect username
   $password = "";
   $database = "php_thenewboston"; // Intentional incorrect database name
   
   class ServerException extends Exception {}
   class DatabaseException extends Exception {}
   
   try {
       // Attempt to establish the connection
       $conn = mysqli_connect($hostname, $username, $password);
       
       // Check connection to MySQL server
       if (!$conn) {
           throw new ServerException('Could not connect to server: ' . mysqli_connect_error());
       }
   
       // Attempt to select the database
       $conn_db = mysqli_select_db($conn, $database);
       
       // Check database selection
       if (!$conn_db) {
           throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn));
       }
   
       echo "Connected successfully.";
   
   } catch (ServerException $ex) {
       // Handle server related exceptions
       echo "Server Error: " . $ex->getMessage();
   } catch (DatabaseException $ex) {
       // Handle database related exceptions
       echo "Database Error: " . $ex->getMessage();
   }
?>