<?php
function getImageFromUrl($url)
{
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    $result = curl_exec($ch);
    if (curl_errno($ch)) {
        // Log error and handle appropriately
        error_log('Curl error: ' . curl_error($ch));
        curl_close($ch);
        return false;
    }
    curl_close($ch);
    return $result;
}

// Set URL and filename
$imageUrl = 'http://example.com/path/to/image.jpg'; // Replace with your image URL
$destinationFolder = __DIR__ . '/img/uploads/';

// Ensure the destination folder exists and is writable
if (!is_dir($destinationFolder) || !is_writable($destinationFolder)) {
    // Handle error: directory doesn't exist or isn't writable
    error_log('Upload directory does not exist or is not writable.');
    exit('Error: Upload directory does not exist or is not writable.');
}

$filename = 'photo1.jpg'; // Set desired filename

// Download and save image
$imageData = getImageFromUrl($imageUrl);
if ($imageData !== false) {
    $fullPath = $destinationFolder . $filename;
    if (file_put_contents($fullPath, $imageData) === false) {
        // Log and handle save error
        error_log('Failed to save image.');
        exit('Error: Failed to save image.');
    } else {
        echo 'Image saved successfully as ' . $fullPath;
    }
} else {
    // Handle error: failed to download image
    error_log('Failed to download image.');
    exit('Error: Failed to download image.');
}
?>