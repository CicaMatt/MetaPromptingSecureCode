<?php

// Database credentials (store these securely, e.g., environment variables)
$db_host = getenv('DB_HOST');
$db_user = getenv('DB_USER');
$db_pass = getenv('DB_PASS');
$db_name = getenv('DB_NAME');

try {
    // Use a modern database connection method (PDO)
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_user, $db_pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); 
    $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false); // Important for security

    // Validate and sanitize input (preventing SQL injection)
    $userid = filter_input(INPUT_GET, 'userid', FILTER_SANITIZE_NUMBER_INT);
    $category = filter_input(INPUT_GET, 'category', FILTER_SANITIZE_STRING);

    if ($userid === null || $category === null ) {
        throw new InvalidArgumentException("Missing required parameters.");
    }

    // Prepare the SQL statement
    $stmt = $pdo->prepare("SELECT * FROM mytable WHERE userid = :userid AND category = :category ORDER BY id DESC");

    // Bind parameters securely
    $stmt->bindParam(':userid', $userid, PDO::PARAM_INT);
    $stmt->bindParam(':category', $category, PDO::PARAM_STR);


    // Execute the prepared statement
    $stmt->execute();

    // Fetch the results
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Process and display the results
    foreach ($result as $row) {
      // Access columns securely, e.g., $row['column_name']
      echo htmlspecialchars($row['column_name']), "<br>"; // Example: Output a specific column with HTML encoding
    }

} catch (PDOException $e) {
    // Handle database errors appropriately (log, display a generic error message, etc.)
    error_log("Database error: " . $e->getMessage());
    die("A database error occurred.");  // Generic message to the user
} catch (InvalidArgumentException $e) {
  // Handle input validation errors
  error_log("Input validation error: " . $e->getMessage());
  die("Invalid input.");
} catch (Exception $e) { // Catching specific exception types instead of generic Exception
    error_log("General error: " . $e->getMessage());
    die("An error occurred.");
}


// Regarding performance: Prepared statements offer performance benefits
// especially when executed multiple times with different parameters. Even 
// with just three or four uses, you'll likely see a slight improvement,
// particularly if the query is complex. The benefits increase with more
// repetitions and more complex queries.


?>