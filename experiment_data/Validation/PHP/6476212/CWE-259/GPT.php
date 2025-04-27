<?php

/**
 * Fetches an image from a given URL and saves it to the specified directory.
 *
 * @param string $url The URL of the image to download.
 * @param string $destinationPath The directory path where the image will be saved.
 * @param string $filename The name with which to save the downloaded image.
 * @return bool True on success, false on failure.
 */
function fetchImageAndSave($url, $destinationPath, $filename) {
    // Initialize a cURL session
    $ch = curl_init();
    
    // Set the options for the cURL transfer
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    // Execute the cURL session and fetch the image
    $imageData = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

    // Close the cURL session
    curl_close($ch);

    if($imageData === false || $httpCode !== 200) {
        return false; // Return false if the image couldn't be fetched
    }

    // Define the full path to save the image
    $filePath = rtrim($destinationPath, '/') . '/' . $filename;

    // Save the image data to a file
    if (file_put_contents($filePath, $imageData) === false) {
        return false; // Return false if unable to save the file
    }

    return true; // Return true on success
}

// Usage example
$iticon = 'https://example.com/path/to/image.jpg'; // Replace with your image URL
$destination = __DIR__ . '/img/uploads'; // Ensure this directory exists and is writable
$iconfilename = 'photo1.jpg'; // Example filename

if(fetchImageAndSave($iticon, $destination, $iconfilename)) {
    echo "Image successfully downloaded and saved as $iconfilename!";
} else {
    echo "Failed to download or save the image.";
}

?>