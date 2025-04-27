<?php

// Configuration (store in a separate, protected file)
$cacheDir = __DIR__ . '/cache/'; // Ensure this directory exists and is writable by the webserver
$cacheLifetime = 180; // Cache lifetime in seconds

// Generate a unique cache key based on the request
$cacheKey = md5($_SERVER['REQUEST_URI']);
$cacheFile = $cacheDir . $cacheKey;

// Check if a valid cached version exists
if (file_exists($cacheFile) && (time() - filemtime($cacheFile)) < $cacheLifetime) {
    // Serve the cached content
    header('Content-type: ' . file_get_contents($cacheFile, false, null, 0, 256)); // Read initial bytes to get Content-Type
    readfile($cacheFile);
    exit;
}

// Start output buffering to capture the dynamic content
ob_start();

// ... Your dynamic PHP code goes here ...

// Example content:
$documentMimeType = 'text/html';
$documentCharset = 'UTF-8';
$currentLanguage = 'en';

header("Content-type: {$documentMimeType}; charset={$documentCharset}");
header('Vary: Accept');
header("Content-language: {$currentLanguage}");

// ... Rest of your dynamic PHP code ...
echo "<h1>Welcome to my website</h1>";
$currentTime = date('Y-m-d H:i:s');
echo "<p>Current time: {$currentTime}</p>";



// Cache the generated content
$cachedContent = ob_get_contents();
ob_end_clean();

// Securely write the cached content to file (handle potential race conditions)
$tempFile = tempnam($cacheDir, 'cache_');
if (false !== $tempFile && file_put_contents($tempFile, $cachedContent) !== false ) {
    if (!rename($tempFile, $cacheFile)) {
        // Handle potential errors, like permission issues
        error_log("Error renaming temporary cache file.");
        unlink($tempFile); // Cleanup the temporary file
    }
} else {
    // Handle errors, e.g., insufficient disk space
    error_log("Error writing to temporary cache file.");
}




// Output the content (first time or cache refresh)
echo $cachedContent;

?>