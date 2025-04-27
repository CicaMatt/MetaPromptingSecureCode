<?php

function saveImageFromUrl($url, $savePath) {

    // Validate inputs to prevent potential path traversal vulnerabilities.
    if (!filter_var($url, FILTER_VALIDATE_URL) || !preg_match('/^[a-zA-Z0-9_\-\.]+$/', basename($savePath))) {
        return false; // Or throw an exception
    }


    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects
    curl_setopt($ch, CURLOPT_MAXREDIRS, 5); // Limit redirects to prevent infinite loops
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36'); // Set a user agent

    // Add error handling for curl
    if (curl_errno($ch)) {
        error_log("cURL error: " . curl_error($ch));
        curl_close($ch);
        return false;
    }


    $imageData = curl_exec($ch);
    curl_close($ch);


    if ($imageData === false) {
      return false; // Or throw an exception
    }


    // Ensure the directory exists; create it if necessary.
    $directory = dirname($savePath);
    if (!is_dir($directory)) {
        mkdir($directory, 0755, true); // Recursive directory creation
    }

    // Save the image data to the specified path
    if (file_put_contents($savePath, $imageData) === false) {
        return false;  // Or throw an exception
    }

    return true;
}



// Example usage:
$imageUrl = "https://www.example.com/image.jpg";  // Replace with the actual image URL
$saveDirectory = __DIR__ . '/img/uploads/'; // Using __DIR__ to get the current script's directory

$filename = 'photo1.jpg'; // Or generate a unique filename if needed, e.g., using uniqid().
$savePath = $saveDirectory . $filename;


if (saveImageFromUrl($imageUrl, $savePath)) {
    echo "Image saved successfully to: " . $savePath;
} else {
    echo "Error saving image.";
}


?>