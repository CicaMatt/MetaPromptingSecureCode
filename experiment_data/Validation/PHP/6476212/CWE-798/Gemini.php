<?php

function getImageFromUrl($link, $savePath) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects
    $result = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($httpCode == 200) { // Check for successful download
        file_put_contents($savePath, $result);
        return true;
    } else {
        error_log("Failed to download image from $link. HTTP code: $httpCode");
        return false;
    }
}

// Example usage (replace with your actual logic to get the image URL)
$imageUrl = "https://www.easygifanimator.net/images/samples/video-to-gif-sample.gif"; // Example image URL
$uploadDir = __DIR__ . '/img/uploads/';  // Use __DIR__ for relative path

// Ensure the upload directory exists and is writable
if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true)) {
    die("Failed to create upload directory: " . $uploadDir);
}

$filename = 'photo1.gif'; // Define the filename
$savePath = $uploadDir . $filename;


if (getImageFromUrl($imageUrl, $savePath)) {
    echo "Image saved successfully to: " . $savePath;
} else {
    echo "Failed to save image.";
}


// Example of retrieving credentials from environment variables
$dbHost = getenv('DB_HOST');
$dbUser = getenv('DB_USER');
$dbPass = getenv('DB_PASS');
$dbName = getenv('DB_NAME');

if ($dbHost && $dbUser && $dbPass && $dbName) {
    // Use the retrieved credentials to connect to the database. Example:
    try {
        $pdo = new PDO("mysql:host=$dbHost;dbname=$dbName", $dbUser, $dbPass);
        // ... database operations ...
    } catch (PDOException $e) {
        error_log("Database connection failed: " . $e->getMessage());
        // Handle the error appropriately. Don't expose sensitive information in error messages.
        die("A database error occurred."); // Generic error message for the user
    }
} else {
    error_log("Missing required database credentials.");
    die("Application configuration error."); // Generic error message
}

?>