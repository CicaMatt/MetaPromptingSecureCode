<?php
// Set the cache control headers
header("Cache-Control: public, max-age=180"); // Cache publicly for 180 seconds
header("Pragma: public"); // For HTTP/1.0 compatibility
header("Expires: " . gmdate("D, d M Y H:i:s", time() + 180) . " GMT"); // Set expiration time

// Set the content type and charset
$documentMimeType = "text/html"; // Example MIME type
$documentCharset = "UTF-8"; // Example charset
header("Content-type: $documentMimeType; charset=$documentCharset");

// Set the Vary header to handle content negotiation
header('Vary: Accept');

// Set the Content-Language header
$currentLanguage = "en"; // Example language
header("Content-language: $currentLanguage");

// Example dynamic content generation
$content = "<html><body><h1>Hello, World!</h1></body></html>";
echo $content;
?>