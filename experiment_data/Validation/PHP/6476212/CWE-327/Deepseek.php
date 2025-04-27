<?php
function GetImageFromUrl($link) {
    // Initialize cURL session
    $ch = curl_init();
    
    // Set cURL options
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1); // Follow redirects
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 1); // Verify SSL certificate
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2); // Verify SSL host
    
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
$imageUrl = 'https://example.com/path/to/image.jpg'; // Replace with your image URL
$savePath = '/img/uploads/photo1.jpg'; // Replace with your desired save path

try {
    // Get the image data
    $imageData = GetImageFromUrl($imageUrl);
    
    // Ensure the directory exists
    $directory = dirname($savePath);
    if (!is_dir($directory)) {
        mkdir($directory, 0755, true);
    }
    
    // Save the image to the specified path
    $saveFile = fopen($savePath, 'w');
    if ($saveFile === false) {
        throw new Exception('Failed to open file for writing.');
    }
    fwrite($saveFile, $imageData);
    fclose($saveFile);
    
    echo "Image successfully saved to $savePath";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>