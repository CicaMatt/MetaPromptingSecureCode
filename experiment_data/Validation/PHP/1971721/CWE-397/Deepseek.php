<?php
// Set the cache control headers to allow caching
header("Cache-Control: public, max-age=180"); // Cache publicly for 180 seconds
header("Pragma: cache"); // For backward compatibility with HTTP/1.0 clients

// Set the expiration time for the cache
$expires = 180; // Cache expiration time in seconds
header("Expires: " . gmdate("D, d M Y H:i:s", time() + $expires) . " GMT");

// Set the content type and charset
$documentMimeType = "text/html"; // Example MIME type
$documentCharset = "UTF-8"; // Example charset
header("Content-type: $documentMimeType; charset=$documentCharset");

// Set the Vary header to indicate that the response may vary based on the Accept header
header('Vary: Accept');

// Set the Content-Language header
$currentLanguage = "en"; // Example language
header("Content-language: $currentLanguage");

// Example dynamic content generation
$dynamicContent = "<html><body><h1>Hello, World!</h1></body></html>";

// Output the content
echo $dynamicContent;
?>
  session_cache_limiter('public');
  session_cache_expire(180);