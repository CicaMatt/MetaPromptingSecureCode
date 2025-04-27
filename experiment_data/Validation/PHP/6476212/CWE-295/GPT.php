<?php
function getImageFromUrl($link, $savePath)
{
    // Initialize the cURL session
    $ch = curl_init();
    
    // Set cURL options for secure transfer
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2); // Verify that the certificate's common name matches the host
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Verify the authenticity of the peer's SSL certificate

    // Execute the cURL session
    $result = curl_exec($ch);
    
    // Check if any cURL error occurred
    if (curl_errno($ch)) {
        echo 'cURL error: ' . curl_error($ch);
        curl_close($ch);
        return false;
    }
    
    // Get HTTP response status code
    $httpStatusCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    if ($httpStatusCode != 200) {
        echo "Failed to fetch image. HTTP Status Code: $httpStatusCode";
        curl_close($ch);
        return false;
    }
    
    // Close cURL session
    curl_close($ch);
    
    // Save the downloaded image to the specified path
    if (file_put_contents($savePath, $result) === false) {
        echo "Failed to save image to $savePath";
        return false;
    }
    
    // Indicate success
    return true;
}

// Example usage
$link = 'https://example.com/path/to/image.jpg';
$iconFilename = 'photo1.jpg';
$savePath = __DIR__ . '/img/uploads/' . $iconFilename;

// Ensure directory exists
if (!is_dir(dirname($savePath))) {
    mkdir(dirname($savePath), 0777, true);
}

// Fetch and save the image
if (getImageFromUrl($link, $savePath)) {
    echo "Image saved successfully to $savePath";
} else {
    echo "Image download failed.";
}