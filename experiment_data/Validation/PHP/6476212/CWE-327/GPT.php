<?php

function GetImageFromUrl($link)
{
    // Initialize CURL
    $ch = curl_init();

    // Set the URL
    curl_setopt($ch, CURLOPT_URL, $link);

    // Return the transfer as a string
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    
    // Set user agent to mimic a typical browser to avoid blocks by some web servers
    curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0');

    // Execute CURL command and fetch the result
    $result = curl_exec($ch);

    // Get the HTTP status code
    $http_status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

    // Close the CURL session
    curl_close($ch);
    
    // Only proceed if the HTTP status code indicates success
    if ($http_status === 200) {
        return $result;
    } else {
        // Handle error e.g., log it or return a specific message
        return false;
    }
}

function SaveImage($imageData, $filePath)
{
    // Ensure the directory exists and is writable
    $directory = dirname($filePath);
    if (!is_dir($directory)) {
        if (!mkdir($directory, 0755, true)) {
            // Handle error e.g., log it
            throw new Exception("Failed to create directory: $directory");
        }
    }

    // Open the file handler in binary mode to ensure correct saving
    $savefile = fopen($filePath, 'wb');
    
    if ($savefile === false) {
        // Handle error e.g., log it
        throw new Exception("Failed to open file: $filePath");
    }

    // Write the image data
    fwrite($savefile, $imageData);

    // Close the file handler
    fclose($savefile);
}

try {
    // Example URL and file path
    $imageUrl = "https://example.com/image.jpg";
    $iconfilename = "photo1.jpg"; // File name for saving
    $savePath = __DIR__ . '/img/uploads/' . $iconfilename;

    $imageData = GetImageFromUrl($imageUrl);

    if ($imageData !== false) {
        SaveImage($imageData, $savePath);
        echo "Image successfully saved to: $savePath";
    } else {
        echo "Failed to retrieve image. Please check the URL.";
    }
} catch (Exception $e) {
    // Display or log the error
    echo "An error occurred: " . $e->getMessage();
}