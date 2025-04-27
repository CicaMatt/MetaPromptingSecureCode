<?php
// Function to set caching headers for better client-side caching
function setCacheHeaders() {
    // Check if the page is cached and hasn't changed since
    $lastModifiedTime = getLastModifiedTime();
    $etag = md5_file(__FILE__);

    // Set headers for client-side caching
    header("Cache-Control: public, max-age=180");
    header("Pragma: cache");
    header("Last-Modified: " . gmdate("D, d M Y H:i:s", $lastModifiedTime) . " GMT");
    header("ETag: \"$etag\"");
    header("Vary: Accept-Encoding");
    header('Expires: ' . gmdate('D, d M Y H:i:s', time() + 180) . ' GMT');

    // Conditional GET for efficient cache validation
    if (isset($_SERVER['HTTP_IF_MODIFIED_SINCE']) && strtotime($_SERVER['HTTP_IF_MODIFIED_SINCE']) >= $lastModifiedTime) {
        if (isset($_SERVER['HTTP_IF_NONE_MATCH']) && trim($_SERVER['HTTP_IF_NONE_MATCH']) == $etag) {
            header("HTTP/1.1 304 Not Modified");
            exit;
        }
    }
}

// Function to get the last modification time
function getLastModifiedTime() {
    // Assuming static files or database modification check is used
    // hardcoded for illustration; replace with actual logic
    return filemtime(__FILE__);
}

// Securely set content type with input validation
$documentMimeType = 'text/html'; // Validate this value if dynamically set
$documentCharset = 'UTF-8';
$currentLanguage = 'en'; // Validate this value if dynamically set

// Sending Headers
header("Content-type: {$documentMimeType}; charset={$documentCharset}");
header("Content-language: {$currentLanguage}");

// Set cache headers
setCacheHeaders();

// Your dynamic content generation code follows here...

?>