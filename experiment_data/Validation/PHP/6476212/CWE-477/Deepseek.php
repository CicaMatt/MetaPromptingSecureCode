<?php
function GetImageFromUrl($link, $savePath)
{
    // Initialize cURL session
    $ch = curl_init($link);
    
    // Set cURL options
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); // Return the result as a string
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1); // Follow redirects
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 1); // Verify SSL certificate
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2); // Verify SSL host
    
    // Execute cURL request
    $result = curl_exec($ch);
    
    // Check for errors
    if (curl_errno($ch)) {
        throw new Exception("cURL Error: " . curl_error($ch));
    }
    
    // Close cURL session
    curl_close($ch);
    
    // Save the image to the specified path
    if (file_put_contents($savePath, $result) === false) {
        throw new Exception("Failed to save the image to $savePath");
    }
    
    return true;
}

// Example usage
try {
    $imageUrl = "https://example.com/path/to/image.jpg"; // Replace with your image URL
    $savePath = __DIR__ . '/img/uploads/photo1.jpg'; // Replace with your desired save path
    
    // Call the function to download and save the image
    if (GetImageFromUrl($imageUrl, $savePath)) {
        echo "Image saved successfully to $savePath";
    }
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>