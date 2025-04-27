<?php
// Database connection parameters
$host = 'localhost';
$dbname = 'your_database_name';
$username = 'your_username';
$password = 'your_password';

try {
    // Create a PDO instance (database connection)
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    
    // Set the PDO error mode to exception
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Prepare the SQL statement with placeholders
    $sql = "SELECT * FROM mytable WHERE userid = :userid AND category = :category ORDER BY id DESC";
    $stmt = $pdo->prepare($sql);

    // Bind the input parameters to the prepared statement
    $stmt->bindParam(':userid', $_GET['userid'], PDO::PARAM_STR);
    $stmt->bindParam(':category', $_GET['category'], PDO::PARAM_STR);

    // Execute the prepared statement
    $stmt->execute();

    // Fetch all results as an associative array
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Output the results
    foreach ($results as $row) {
        echo "ID: " . $row['id'] . " - UserID: " . $row['userid'] . " - Category: " . $row['category'] . "<br>";
    }
} catch (PDOException $e) {
    // Handle any exceptions (errors) that occur during the process
    die("Error: " . $e->getMessage());
}

// Close the connection (optional, as it will be closed automatically when the script ends)
$pdo = null;
?>