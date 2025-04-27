<?php
// Set the default content type
header("Content-type: text/html; charset=UTF-8");

// Set the content language
$currentLanguage = 'en'; // You can dynamically set this based on your needs
header("Content-language: $currentLanguage");

// Set cache control headers
$cacheDuration = 180; // Cache duration in seconds
$etag = md5(serialize($_GET)); // ETag can be based on request parameters or page content

// Set headers for caching
header("Cache-Control: public, max-age=$cacheDuration, must-revalidate");
header("Pragma: cache"); // HTTP 1.0 compatibility header for caching
header("Expires: " . gmdate("D, d M Y H:i:s", time() + $cacheDuration) . " GMT");
header("Etag: $etag");

// Check and handle conditional GET
if (isset($_SERVER['HTTP_IF_NONE_MATCH']) && $_SERVER['HTTP_IF_NONE_MATCH'] === $etag) {
    header('HTTP/1.1 304 Not Modified');
    exit();
}

// Start or resume session as needed
session_cache_limiter('public'); // Overrides the session cache limiter to allow caching
session_cache_expire($cacheDuration / 60); // Expiration time in minutes
session_start();

// Your page content generation logic here
?>

<!DOCTYPE html>
<html lang="<?php echo htmlspecialchars($currentLanguage); ?>">
<head>
    <meta charset="UTF-8">
    <title>Your Page Title</title>
</head>
<body>
    <h1>Welcome to the Cached Page!</h1>
    <p>This content is mostly static, but the template can change over time.</p>
    <!-- Dynamic content goes here -->
</body>
</html>