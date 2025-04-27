<?php

function GetImageFromUrl($link, $savePath)
{
    try {
        $ch = curl_init();
        if ($ch === false) {
            throw new Exception("Failed to initialize cURL");
        }

        curl_setopt($ch, CURLOPT_POST, 0);
        curl_setopt($ch, CURLOPT_URL, $link);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects

        $result = curl_exec($ch);

        if ($result === false) {
            throw new Exception("cURL error: " . curl_error($ch));
        }

        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        if ($httpCode >= 400) {
            throw new Exception("HTTP error: " . $httpCode); 
        }


        curl_close($ch);

        if (file_put_contents($savePath, $result) === false) {
            throw new Exception("Failed to save image to file: " . $savePath);
        }

        return true; // Indicate success

    } catch (Exception $e) {
        // Log the error for debugging
        error_log("Error downloading image: " . $e->getMessage());
        return false; // Indicate failure
    }
}


// Example usage
$imageUrl = "https://www.easygifanimator.net/images/samples/video-to-gif-sample.gif"; // Example GIF URL (Replace with your URL)
$savePath =  __DIR__ . '/img/uploads/photo1.gif'; // Save path, including filename and extension
$iconfilename = 'photo1.gif';


if (!is_dir(dirname($savePath))) {
    mkdir(dirname($savePath), 0777, true); // Create directories if they don't exist with appropriate permissions (adjust as needed)
}


if (GetImageFromUrl($imageUrl, $savePath)) {
    echo "Image downloaded successfully to $iconfilename";
} else {
    echo "Failed to download image. Check the error log for details.";
}



?>