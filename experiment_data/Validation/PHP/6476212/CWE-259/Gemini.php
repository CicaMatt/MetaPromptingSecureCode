<?php

function getImageFromUrl($link, $savePath) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true); // Follow redirects
    curl_setopt($ch, CURLOPT_MAXREDIRS, 5); // Limit redirects to prevent loops
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Verify SSL certificates
    $result = curl_exec($ch);

    if (curl_errno($ch)) {
        error_log("cURL error: " . curl_error($ch)); 
        return false;
    }

    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    if ($httpCode != 200) {
        error_log("HTTP error: " . $httpCode);
        return false;
    }


    curl_close($ch);
    return $result;
}

// Example usage (replace with your actual values and error handling)
$imageUrl = $_GET['imageUrl'] ?? null; // Get URL from a safe source (e.g., not directly from user input without validation)

if ($imageUrl) {
    // Validate URL (e.g., check if it's a valid image URL, uses HTTPS, etc.)

    $uploadDir = __DIR__ . '/img/uploads/'; // Make sure directory exists and is writable by the webserver
    $allowedExtensions = ['jpg', 'jpeg', 'png', 'gif']; // Allow only safe image extensions
    $filename = 'photo1.jpg'; // Or generate a unique, safe filename

    $parsedUrl = parse_url($imageUrl);
    $ext = pathinfo($parsedUrl['path'], PATHINFO_EXTENSION);

    if (in_array(strtolower($ext), $allowedExtensions)) {
        $imagePath = $uploadDir . $filename;
        $imageData = getImageFromUrl($imageUrl, $imagePath);


        if ($imageData) {
            if (file_put_contents($imagePath, $imageData) !== false) {
                echo "Image saved successfully!";
            } else {
                error_log("Error saving image to file.");
                echo "Error saving image.";
            }
        } else {
             echo "Error downloading image.";
        }

    } else {
        echo "Invalid image URL or file type.";
    }


} else {
    echo "Image URL not provided.";
}


?>