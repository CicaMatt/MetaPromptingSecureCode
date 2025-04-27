<?php

function GetImageFromUrl($link) {
    // Initialize cURL session
    $ch = curl_init();
    
    // Set cURL options
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false); // Use only if the URL is HTTPS and you have SSL issues
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects
    
    // Execute cURL request
    $result = curl_exec($ch);
    
    // Check for cURL errors
    if (curl_errno($ch)) {
        throw new Exception('cURL error: ' . curl_error($ch));
    }
    
    // Close cURL session
    curl_close($ch);
    
    return $result;
}

// Example usage
try {
    $imageUrl = 'https://example.com/path/to/image.jpg'; // Replace with the actual image URL
    $savePath = '/img/uploads/photo1.jpg'; // Replace with the desired save path and filename
    
    // Ensure the directory exists
    $directory = dirname($savePath);
    if (!is_dir($directory)) {
        mkdir($directory, 0755, true);
    }
    
    // Get the image from the URL
    $imageData = GetImageFromUrl($imageUrl);
    
    // Save the image to the specified path
    if (file_put_contents($savePath, $imageData)) {
        echo "Image saved successfully to $savePath";
    } else {
        throw new Exception("Failed to save image to $savePath");
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}

?>