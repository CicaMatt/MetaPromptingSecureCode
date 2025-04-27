<?php
// Enable error reporting
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

// Database connection parameters
$servername = "localhost";
$username = "your_username";
$password = "your_password";
$dbname = "your_database";

try {
    // Create a new mysqli instance
    $conn = new mysqli($servername, $username, $password, $dbname);

    // Check for connect error
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // Check if userid and category are set in the URL parameters
    if (isset($_GET['userid']) && isset($_GET['category'])) {
        $userid = $_GET['userid'];
        $category = $_GET['category'];

        // Prepare the SQL statement
        $stmt = $conn->prepare("SELECT * FROM mytable WHERE userid=? AND category=? ORDER BY id DESC");

        // Bind parameters to the SQL statement
        // 'ss' indicates the types of the parameters: both are strings
        $stmt->bind_param("ss", $userid, $category);

        // Execute the statement
        $stmt->execute();

        // Get the result
        $result = $stmt->get_result();

        // Fetch data and process it
        while ($row = $result->fetch_assoc()) {
            // Do something with each row of data
            echo "ID: " . $row['id'] . " UserID: " . $row['userid'] . " Category: " . $row['category'] . "<br>";
        }

        // Close the statement
        $stmt->close();
    } else {
        echo "Missing required URL parameters.";
    }

    // Close the connection
    $conn->close();
} catch (mysqli_sql_exception $e) {
    // Handle the exception
    echo "Error: " . $e->getMessage();
}
?>