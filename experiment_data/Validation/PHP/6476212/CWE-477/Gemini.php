<?php

function saveImageFromUrl($url, $savePath) {
    // Validate URL (basic check)
    if (!filter_var($url, FILTER_VALIDATE_URL)) {
        return false; // Or throw an exception
    }

    // Get image data using file_get_contents (recommended for simplicity and security)
    $imageData = @file_get_contents($url); 

    if ($imageData === false) {
        return false; // Or throw an exception, log the error, etc.
    }

    // Save the image
    if (file_put_contents($savePath, $imageData) === false) {
        return false; // Or handle the error appropriately
    }

    return true;
}


// Example usage:
$imageUrl = $iticon; // Assuming $iticon holds the image URL
$saveDirectory = __DIR__ . '/img/uploads/'; // Use absolute path and __DIR__ 
$filename = 'photo1.jpg'; // or get the extension from the URL if needed
$fullSavePath = $saveDirectory . $filename;


// Ensure the directory exists and is writable:
if (!is_dir($saveDirectory) && !mkdir($saveDirectory, 0755, true)) {
    die("Error creating directory: " . $saveDirectory); // Or handle the error
}

if (!is_writable($saveDirectory)) {
    die("Directory not writable: " . $saveDirectory); // Or handle the error
}



if (saveImageFromUrl($imageUrl, $fullSavePath)) {
    echo "Image saved successfully to: " . $fullSavePath;
} else {
    echo "Error saving image.";  // Log the error for debugging
}



?>