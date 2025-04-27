<?php

// Function to retrieve data using a prepared statement
function getData($conn, $userid, $category) {
    try {
        $stmt = $conn->prepare("SELECT * FROM mytable WHERE userid = ? AND category = ? ORDER BY id DESC");
        if (!$stmt) {
            throw new Exception("Prepare failed: (" . $conn->errno . ") " . $conn->error);
        }

        $stmt->bind_param("ss", $userid, $category); // "ss" indicates both parameters are strings
        if (!$stmt->execute()) {
            throw new Exception("Execute failed: (" . $stmt->errno . ") " . $stmt->error);
        }

        $result = $stmt->get_result();
        if (!$result) {
            throw new Exception("Get result failed: (" . $stmt->errno . ") " . $stmt->error);
        }

        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }

        $stmt->close();
        return $data;

    } catch (Exception $e) {
        // Log the error for debugging
        error_log("Database error: " . $e->getMessage());

        // Handle the error appropriately for the user – e.g., display a generic error message
        return false; // Or throw a custom exception to be handled higher up
    }
}


// Example usage (assuming you have a database connection $conn)
if (isset($_GET['userid']) && isset($_GET['category'])) {
    $userid = $_GET['userid'];
    $category = $_GET['category'];


    // Validate inputs before using them in the query (e.g., sanitize for SQL injection)
    // Example validation (replace with your specific validation logic):
    $userid = htmlspecialchars($userid);
    $category = htmlspecialchars($category);


    $data = getData($conn, $userid, $category);

    if ($data !== false) {
        // Process the retrieved data
        foreach ($data as $row) {
           // ... do something with each row ...
            print_r($row);
        }

    } else {
        echo "Error retrieving data."; // Or a more user-friendly message
    }


} else {
    echo "Missing parameters.";
}

?>