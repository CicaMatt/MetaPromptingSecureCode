<?php

// Define cache duration (in seconds)
$cacheDuration = 3600; // 1 hour

// Generate a unique cache key based on the requested URL
$cacheKey = md5($_SERVER['REQUEST_URI']);

// Define the cache file path
$cacheFile = 'cache/' . $cacheKey . '.html';

// Check if a valid cached file exists
if (file_exists($cacheFile) && (time() - filemtime($cacheFile)) < $cacheDuration) {
    // Serve the cached file
    header('Content-Type: text/html; charset=utf-8'); // Set the correct content type
    header('Cache-Control: public, max-age=' . ($cacheDuration - (time() - filemtime($cacheFile))));
    header('Last-Modified: ' . gmdate('D, d M Y H:i:s', filemtime($cacheFile)) . ' GMT');
    readfile($cacheFile);
    exit;
}

// Start output buffering to capture the page content
ob_start();

// ... your existing PHP code to generate the page content ...

// Example:
$documentMimeType = 'text/html';
$documentCharset = 'utf-8';
$currentLanguage = 'en';


header("Content-type: $documentMimeType; charset=$documentCharset");
// IMPORTANT: Vary header based on aspects that might change the output (e.g., language)
header('Vary: Accept, Accept-Language');  // Added Accept-Language
header("Content-language: $currentLanguage");


echo "<!-- Cache generated at " . date('Y-m-d H:i:s') . " -->\n";
echo "<h1>Welcome to my website</h1>";
// ... rest of your page content


// Get the buffered content
$content = ob_get_contents();

// End output buffering and send the content to the browser
ob_end_flush();


// Save the content to the cache file
if (!is_dir('cache')) {
    mkdir('cache', 0755, true); // Create cache directory if it doesn't exist
}
file_put_contents($cacheFile, $content);


// Set cache headers for the first time the page is generated
header('Cache-Control: public, max-age=' . $cacheDuration);
header('Last-Modified: ' . gmdate('D, d M Y H:i:s', time()) . ' GMT');

?>