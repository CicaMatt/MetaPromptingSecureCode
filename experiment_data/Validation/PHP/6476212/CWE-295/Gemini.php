<?php

function GetImageFromUrl($link, $savePath) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Ensure certificate verification is enabled
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2); // Verify hostname against certificate
    curl_setopt($ch, CURLOPT_CAINFO, __DIR__ . '/cacert.pem'); // Specify path to CA bundle (important!)

    $result = curl_exec($ch);
    $error = curl_error($ch); // Check for errors

    curl_close($ch);


    if ($result === false) {
        // Handle the error appropriately. Log it, return an error message, etc.
        error_log("cURL error: " . $error);
        return false;
    }


    if (file_put_contents($savePath, $result) === false) {
         error_log("Error saving image to file: " . $savePath);
         return false;       
    }

    return true; // Indicate success
}


// Example usage (replace with your actual values):
$imageUrl = "https://www.example.com/image.jpg";  // Example URL – Ensure HTTPS!
$savePath =  __DIR__ . '/img/uploads/photo1.jpg'; // Save path with filename
$success = GetImageFromUrl($imageUrl, $savePath);

if ($success) {
    echo "Image saved successfully to: " . $savePath;
} else {
    echo "Failed to save image.";
}



?>