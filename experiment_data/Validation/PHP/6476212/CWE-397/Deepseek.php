<?php
function GetImageFromUrl($link) {
    // Initialize cURL session
    $ch = curl_init();
    if ($ch === false) {
        throw new Exception("Failed to initialize cURL.");
    }

    // Set cURL options
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Enable SSL verification for security
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects

    // Execute cURL request
    $result = curl_exec($ch);
    if ($result === false) {
        $error = curl_error($ch);
        curl_close($ch);
        throw new Exception("cURL error: " . $error);
    }

    // Check HTTP status code
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    if ($httpCode !== 200) {
        curl_close($ch);
        throw new Exception("Failed to fetch image. HTTP status code: " . $httpCode);
    }

    // Close cURL session
    curl_close($ch);

    return $result;
}

try {
    // Example URL and filename
    $imageUrl = "https://example.com/path/to/image.jpg";
    $savePath = __DIR__ . '/img/uploads/photo1.jpg'; // Use absolute path for security

    // Ensure the directory exists
    if (!is_dir(dirname($savePath))) {
        throw new Exception("Directory does not exist: " . dirname($savePath));
    }

    // Fetch the image
    $imageData = GetImageFromUrl($imageUrl);

    // Save the image to the specified path
    $saveFile = fopen($savePath, 'w');
    if ($saveFile === false) {
        throw new Exception("Failed to open file for writing: " . $savePath);
    }

    fwrite($saveFile, $imageData);
    fclose($saveFile);

    echo "Image saved successfully to: " . $savePath;
} catch (Exception $e) {
    // Handle exceptions
    echo "Error: " . $e->getMessage();
}
?>