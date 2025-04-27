<?php

// Database connection details (store these securely, e.g., in environment variables)
$db_host = getenv('DB_HOST');
$db_user = getenv('DB_USER');
$db_pass = getenv('DB_PASS');
$db_name = getenv('DB_NAME');

// Establish database connection using mysqli (mysql_ functions are deprecated)
$conn = new mysqli($db_host, $db_user, $db_pass, $db_name);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


// Input validation and sanitization (critical for security)
$userid = isset($_GET['userid']) ? htmlspecialchars($_GET['userid']) : '';  // Basic sanitization
$category = isset($_GET['category']) ? htmlspecialchars($_GET['category']) : ''; // Basic sanitization

// Even better: Parameterized queries with prepared statements
if (!empty($userid) && !empty($category)) {  // Only proceed if both parameters are present


    $stmt = $conn->prepare("SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC");
    if (!$stmt) {
        die("Prepare failed: " . $conn->error); // Handle prepare errors
    }

    $stmt->bind_param("ss", $userid, $category); // "ss" indicates string parameters
    if (!$stmt->execute()) {
        die("Execute failed: " . $stmt->error);  // Handle execution errors
    }
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            // Process the retrieved data securely
            // ... e.g., echo htmlspecialchars($row['column_name']);
        }
    } else {
        echo "No results found.";
    }
    $stmt->close();


} else {
    echo "Missing required parameters.";
}

$conn->close();


/* Regarding the bonus question about performance:

Prepared statements *can* improve performance, especially when the same query 
is executed multiple times with different parameters.  The database can reuse the 
query plan, reducing overhead. However, for only 3-4 executions on a single page, 
the difference might be negligible.  The primary benefit of prepared statements 
is security against SQL injection.
*/

?>