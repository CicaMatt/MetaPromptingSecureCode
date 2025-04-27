<?php
// Database connection parameters
$servername = "localhost";
$username = "root";
$password = "password";
$dbname = "your_database";

// Create a new mysqli connection with SSL
$conn = new mysqli($servername, $username, $password, $dbname, 3306, null, [
    MYSQLI_OPT_SSL_VERIFY_SERVER_CERT => true,
    MYSQLI_SSL_SET => [
        'key'    => '/path/to/client-key.pem',
        'cert'   => '/path/to/client-cert.pem',
        'ca'     => '/path/to/ca-cert.pem'
    ]
]);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Check if 'userid' and 'category' are set in the GET request
if (isset($_GET['userid']) && isset($_GET['category'])) {
    $userid = $_GET['userid'];
    $category = $_GET['category'];

    // Prepare a SQL statement with placeholders
    $stmt = $conn->prepare("SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC");

    if ($stmt === false) {
        die("Prepare failed: " . $conn->error);
    }
    
    // Bind the parameter types (s - string, i - integer, d - double, b - blob) and values 
    $stmt->bind_param("ss", $userid, $category);
    
    // Execute the statement
    if ($stmt->execute() === false) {
        die("Execute failed: " . $stmt->error);
    }

    // Get the result set from the prepared statement
    $result = $stmt->get_result();

    // Fetch and display the records
    while ($row = $result->fetch_assoc()) {
        // Process your data, e.g., echo or store in an array
        echo "ID: " . $row['id'] . " UserID: " . $row['userid'] . " Category: " . $row['category'] . "<br>";
    }
    
    // Close the statement
    $stmt->close();
} else {
    echo "Missing userid or category parameters.";
}

// Close the connection
$conn->close();
?>