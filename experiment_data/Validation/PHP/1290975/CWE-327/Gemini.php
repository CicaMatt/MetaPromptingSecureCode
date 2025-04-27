<?php

// Establish a database connection (replace with your credentials)
$conn = new mysqli("localhost", "your_username", "your_password", "your_database");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Prepare the SQL statement
$stmt = $conn->prepare("SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC");

if (!$stmt) {
    die("Prepare failed: (" . $conn->errno . ") " . $conn->error); 
}


// Sanitize input (important to prevent SQL injection)
$userid = $conn->real_escape_string($_GET['userid']); 
$category = $conn->real_escape_string($_GET['category']);



// Bind parameters
$stmt->bind_param("ss", $userid, $category); // "ss" indicates two string parameters


// Execute the prepared statement
if ($stmt->execute()) {
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            // Process the retrieved data
            print_r($row); // Example: print each row
        }
    } else {
        echo "0 results";
    }

    $result->free(); // Free the result set

} else {
    die("Execute failed: (" . $stmt->errno . ") " . $stmt->error);
}

$stmt->close();
$conn->close();



/* Regarding the speed bonus:
Prepared statements offer performance benefits primarily in scenarios with repeated execution of the same query with different parameters.  The database server can cache the query plan, reducing the overhead of parsing and optimizing the SQL each time.  

For only three or four executions on a page, the performance gain might be negligible.  However, it's still a best practice to use prepared statements for their security benefits (preventing SQL injection). If you anticipate scaling up with many queries, the benefits will become more significant. */

?>