<?php

// ... your existing PHP code ...

// Determine if the page can be cached based on dynamic content conditions
$cacheable = true; // Set to false if the page content depends on user-specific data, sessions, etc.


if ($cacheable) {
    $cache_key = md5($_SERVER['REQUEST_URI']); // Create a unique cache key based on the requested URL
    $cache_file = 'cache/' . $cache_key; // Store cache files in a dedicated directory

    if (file_exists($cache_file) && (time() - filemtime($cache_file)) < 180) { // Check if a valid cache file exists (180 seconds expiry)
        // Serve the cached content
        header('Content-type: ' . $documentMimeType . '; charset=' . $documentCharset);
        header('Content-language: ' . $currentLanguage);
        // Important: Add Cache-Control headers
        header('Cache-Control: public, max-age=180'); // Allow public caching for 180 seconds
        header('Last-Modified: ' . gmdate('D, d M Y H:i:s', filemtime($cache_file)) . ' GMT');  // Tell browsers when the resource was last modified
        header('ETag: "' . md5_file($cache_file) . '"');  // Provide an ETag for efficient caching validation
        readfile($cache_file);
        exit;
    } else {
        // Generate and cache the page content

        ob_start(); // Start output buffering

        // ... your page generation logic ... 

        $page_content = ob_get_contents(); // Get the buffered output
        ob_end_clean(); // End and clear output buffering

        file_put_contents($cache_file, $page_content); // Save the content to the cache file

        // Send headers (same as above for cached content) including Cache-Control, Last-Modified, and ETag.
        header('Content-type: ' . $documentMimeType . '; charset=' . $documentCharset);
        header('Content-language: ' . $currentLanguage);
        header('Cache-Control: public, max-age=180');
        header('Last-Modified: ' . gmdate('D, d M Y H:i:s', filemtime($cache_file)) . ' GMT');
        header('ETag: "' . md5_file($cache_file) . '"');
        echo $page_content;
    }
} else {
    // Generate the page content dynamically (without caching)
    // ... your page generation logic ...
    header('Content-type: ' . $documentMimeType . '; charset=' . $documentCharset);
    header('Content-language: ' . $currentLanguage); 
    header('Cache-Control: no-store, no-cache, must-revalidate, max-age=0'); // Prevent caching for dynamic content
}



// ... rest of your PHP code ...

?>