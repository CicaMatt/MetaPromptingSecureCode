<?php

// Assuming you have a database connection established.
// Replace 'your_db_host', 'your_db_user', 'your_db_password', and 'your_db_name' with your actual credentials.
$conn = new mysqli('your_db_host', 'your_db_user', 'your_db_password', 'your_db_name');

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Prepare the statement
$stmt = $conn->prepare("SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC");

if (!$stmt) {
    die("Prepare failed: (" . $conn->errno . ") " . $conn->error);
}



// Sanitize and bind parameters.  Use htmlspecialchars to prevent XSS vulnerabilities if displaying the output directly in HTML.
$userid = htmlspecialchars($_GET['userid'] ?? '', ENT_QUOTES, 'UTF-8'); // Also handle cases where 'userid' is not set in $_GET
$category = htmlspecialchars($_GET['category'] ?? '', ENT_QUOTES, 'UTF-8');  // Also handle cases where 'category' is not set in $_GET


$stmt->bind_param("ss", $userid, $category); // "ss" indicates two string parameters


// Execute
$stmt->execute();

// Get the result set
$result = $stmt->get_result();


// Fetch data
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row; 
}

// Close the statement and connection
$stmt->close();
$conn->close();


// Example: Output data as JSON (You can modify this to suit your needs)
header('Content-Type: application/json');
echo json_encode($data);


/*
BONUS Answer: The performance benefits of prepared statements are most noticeable when you execute the *same* prepared statement multiple times with different parameters.  The database can reuse the query plan, leading to optimization. If you're only using a prepared statement three or four times on a page with *different* SQL queries each time, the overhead of preparing the statement might outweigh the performance gains.  It depends on the complexity of the queries and other factors. Benchmarking is always recommended to see if prepared statements offer a significant speed increase in your specific use case. For just a few executions, the performance difference might be negligible.

Prepared statements are primarily for *security*, preventing SQL injection.  The performance improvement is a secondary benefit.
*/

?>