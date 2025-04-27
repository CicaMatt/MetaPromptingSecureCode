<?php
   $hostname = "localhost";
   $username = "root1";
   $password = "";
   $database = "php_thenewboston";

   class ServerException extends Exception{}  
   class DatabaseException extends Exception{}
   class UsernameException extends Exception{} // New exception for incorrect username

   try{
       $conn = mysqli_connect($hostname,$username,$password);
       if(!$conn){
           throw new ServerException('Could not connect to server.');
       }

       $conn_db = mysqli_select_db($conn,$database); 
       if(!$conn_db){
           throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn)); // Show MySQL error
       }

       // Check if the connection was successful and username is valid.
       //  We do a dummy query to provoke a username error if it exists.
       $result = mysqli_query($conn, "SELECT 1");  // Simple query
       if(!$result){
           if (mysqli_errno($conn) == 1045) { // Access denied for user
             throw new UsernameException('Could not connect to database: Invalid username or password.');
           } else {
               throw new DatabaseException('Database error: ' . mysqli_error($conn)); // Other database errors
           }
       }


       echo "Connected.";


   }catch(ServerException $ex){
       echo "Error :".$ex->getMessage();        
   }catch(DatabaseException $ex){
       echo "Error :".$ex->getMessage();
   } catch(UsernameException $ex){
       echo "Error :".$ex->getMessage();
   } finally { // Good practice to close the connection in a finally block
       if (isset($conn)) {
           mysqli_close($conn);
       }
   }
?>