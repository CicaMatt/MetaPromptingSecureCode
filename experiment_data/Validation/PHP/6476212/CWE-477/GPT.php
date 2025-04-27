<?php

function saveImageFromUrl($url, $savePath) {
    // Initialize a cURL session
    $ch = curl_init($url);

    // Check if the URL is valid
    if (filter_var($url, FILTER_VALIDATE_URL) === false) {
        throw new Exception("Invalid URL.");
    }

    // Set cURL options
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Verify the SSL certificate
    curl_setopt($ch, CURLOPT_CAINFO, __DIR__ . '/path/to/cacert.pem'); // Path to a certificate file

    // Execute the cURL request
    $data = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

    // Check for cURL errors
    if (curl_errno($ch)) {
        throw new Exception("cURL Error: " . curl_error($ch));
    }

    // Check for valid HTTP response
    if ($httpCode != 200) {
        throw new Exception("Failed to download image, HTTP Status Code: " . $httpCode);
    }

    // Close the cURL session
    curl_close($ch);

    // Ensure save directory exists
    $saveDir = dirname($savePath);
    if (!is_dir($saveDir)) {
        if (!mkdir($saveDir, 0755, true)) {
            throw new Exception("Failed to create directory: " . $saveDir);
        }
    }

    // Save the file
    if (file_put_contents($savePath, $data) === false) {
        throw new Exception("Failed to save image to: " . $savePath);
    }

    return true;
}

try {
    $imageUrl = "http://example.com/image.jpg"; // Replace with the actual image URL
    $saveAs = __DIR__ . '/img/uploads/photo1.jpg'; // Path where the image will be saved

    saveImageFromUrl($imageUrl, $saveAs);

    echo "Image downloaded and saved successfully.";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}