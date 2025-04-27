<?php
function GetImageFromUrl($link) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false); // Use only if the URL is HTTPS and you trust the source
    $result = curl_exec($ch);
    if (curl_errno($ch)) {
        // Handle cURL error
        throw new Exception('cURL error: ' . curl_error($ch));
    }
    curl_close($ch);
    return $result;
}

function SaveImageToServer($imageData, $filePath) {
    $savefile = fopen($filePath, 'w');
    if (!$savefile) {
        throw new Exception('Unable to open file for writing: ' . $filePath);
    }
    fwrite($savefile, $imageData);
    fclose($savefile);
}

try {
    // Example URL and file path
    $imageUrl = 'https://example.com/path/to/image.jpg'; // Replace with your image URL
    $savePath = __DIR__ . '/img/uploads/photo1.jpg'; // Replace with your desired save path

    // Ensure the directory exists
    if (!is_dir(dirname($savePath))) {
        mkdir(dirname($savePath), 0755, true);
    }

    // Download the image
    $imageData = GetImageFromUrl($imageUrl);

    // Save the image to the server
    SaveImageToServer($imageData, $savePath);

    echo "Image successfully saved to: " . $savePath;
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>