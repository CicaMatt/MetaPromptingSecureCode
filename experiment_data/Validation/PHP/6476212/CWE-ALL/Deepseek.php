<?php

/**
 * Function to securely download an image from a URL and save it to a specified directory.
 * 
 * @param string $url The URL of the image to download.
 * @param string $savePath The directory path where the image will be saved.
 * @param string $fileName The name of the file to save the image as.
 * @return bool Returns true on success.
 * @throws InvalidUrlException If the provided URL is invalid.
 * @throws CurlException If there is an error with the cURL request.
 * @throws HttpException If the HTTP response code is not 200.
 * @throws FileSaveException If the file cannot be saved.
 */
function downloadImageFromUrl($url, $savePath, $fileName) {
    // Validate the URL to ensure it is a valid image URL
    if (!filter_var($url, FILTER_VALIDATE_URL)) {
        throw new InvalidUrlException("Invalid URL provided: $url");
    }

    // Initialize cURL
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);

    // Execute cURL request
    $imageData = curl_exec($ch);

    // Check for cURL errors
    if (curl_errno($ch)) {
        throw new CurlException("cURL error: " . curl_error($ch));
    }

    // Get the HTTP status code
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($httpCode !== 200) {
        throw new HttpException("Failed to download image. HTTP status code: " . $httpCode);
    }

    // Ensure the save directory exists and is writable
    if (!is_dir($savePath) || !is_writable($savePath)) {
        throw new FileSaveException("Save directory does not exist or is not writable: $savePath");
    }

    // Sanitize the file name
    $fileName = preg_replace('/[^a-zA-Z0-9\-_\.]/', '', $fileName);
    $filePath = rtrim($savePath, '/') . '/' . $fileName;

    // Save the image
    if (file_put_contents($filePath, $imageData) === false) {
        throw new FileSaveException("Failed to save image to file: $filePath");
    }

    return true;
}

// Example usage
try {
    $imageUrl = "https://example.com/path/to/image.jpg";
    $saveDirectory = "/img/uploads";
    $fileName = "photo1.jpg";

    if (downloadImageFromUrl($imageUrl, $saveDirectory, $fileName)) {
        echo "Image downloaded and saved successfully.";
    }
} catch (InvalidUrlException $e) {
    echo "URL Error: " . $e->getMessage();
} catch (CurlException $e) {
    echo "cURL Error: " . $e->getMessage();
} catch (HttpException $e) {
    echo "HTTP Error: " . $e->getMessage();
} catch (FileSaveException $e) {
    echo "File Save Error: " . $e->getMessage();
} catch (DownloadException $e) {
    echo "General Download Error: " . $e->getMessage();
} catch (Exception $e) {
    echo "Unexpected Error: " . $e->getMessage();
}

?>
