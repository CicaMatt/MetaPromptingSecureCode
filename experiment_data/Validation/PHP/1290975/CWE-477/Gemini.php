<?php

// Database connection details (replace with your own credentials)
$host = 'your_host';
$username = 'your_username';
$password = 'your_password';
$dbname = 'your_database';

try {
    // Create a new PDO connection
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);

    // Set PDO to throw exceptions for error handling
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Prepare the SQL statement
    $stmt = $pdo->prepare("SELECT * FROM mytable WHERE userid = :userid AND category = :category ORDER BY id DESC");

    // Get the input parameters safely 
    $userid = isset($_GET['userid']) ? $_GET['userid'] : null; // Handle missing parameters
    $category = isset($_GET['category']) ? $_GET['category'] : null;

    // Bind the parameters to the prepared statement
    $stmt->bindValue(':userid', $userid, PDO::PARAM_STR); // Use appropriate data type
    $stmt->bindValue(':category', $category, PDO::PARAM_STR); // Use appropriate data type

    // Execute the prepared statement
    $stmt->execute();

    // Fetch the results (e.g., as an associative array)
    $results = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Process the results
    foreach ($results as $row) {
        // Access data using column names, e.g., $row['column_name']
        // ... your code to handle the retrieved data ...
    }

} catch (PDOException $e) {
    // Handle database errors
    die("Database error: " . $e->getMessage());
}

// Regarding the speed improvement:
// While prepared statements offer some performance benefits due to query plan caching,
// the improvement might not be substantial for only three or four executions on a page.
//  The major benefit of prepared statements is increased security by preventing SQL injection.

?>