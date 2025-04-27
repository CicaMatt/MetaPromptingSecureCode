<?php
// Cache settings
$cacheDuration = 3600; // Cache duration in seconds (1 hour)
$lastModifiedTime = filemtime(__FILE__); // Assuming the file's last modified time for demo purposes
$etag = md5_file(__FILE__); // Using file's MD5 hash as an ETag for demo purposes

// Set headers for caching
header("Cache-Control: public, max-age=$cacheDuration"); // Public allow browser/proxy caching
header("Pragma: cache"); // HTTP/1.0 backward compatibility
header("Expires: " . gmdate("D, d M Y H:i:s", time() + $cacheDuration) . " GMT");
header("Last-Modified: " . gmdate("D, d M Y H:i:s", $lastModifiedTime) . " GMT");
header("Etag: $etag");

// Evaluate sent headers from client for conditional GET
if (@strtotime($_SERVER['HTTP_IF_MODIFIED_SINCE']) == $lastModifiedTime ||
    trim($_SERVER['HTTP_IF_NONE_MATCH']) == $etag) {
    // Client's cache is up-to-date, no need to resend the resource, send 304 Not Modified
    header("HTTP/1.1 304 Not Modified");
    exit;
}

// Contents of the actual page
echo "<html>";
echo "<head><title>Your Page</title></head>";
echo "<body>";
echo "<h1>Welcome to the cached page</h1>";
echo "<p>This content is cached for $cacheDuration seconds.</p>";
echo "</body>";
echo "</html>";