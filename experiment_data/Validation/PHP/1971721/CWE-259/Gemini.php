<?php

// Configuration (store outside the webroot in a secure location)
require_once('../config/config.php'); // Example path

// Database connection (using PDO and prepared statements)
try {
    $pdo = new PDO("mysql:host=" . DB_HOST . ";dbname=" . DB_NAME, DB_USER, DB_PASS);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage()); 
}

// Caching (using file-based caching - adjust path as needed)
$cacheDir = '../cache/';
$cacheFile = $cacheDir . md5($_SERVER['REQUEST_URI']) . '.html';
$cacheTime = 3600; // 1 hour

if (file_exists($cacheFile) && time() - filemtime($cacheFile) < $cacheTime) {
    // Serve cached content
    header('Content-Type: text/html; charset=utf-8');
    readfile($cacheFile); 
    exit;
}

ob_start(); // Start output buffering

// ... your PHP code to generate dynamic content ...

// Example of fetching data from the database:
$stmt = $pdo->prepare("SELECT * FROM content WHERE page_id = ?"); 
$stmt->execute([$_GET['page_id']]); // Parameterized query to prevent SQL injection
$content = $stmt->fetch(PDO::FETCH_ASSOC);


// HTML Output (Example)
?><!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Example</title>
     <!-- Cache control headers (important) -->
    <meta http-equiv="Cache-Control" content="public, max-age=<?php echo $cacheTime; ?>">
    <meta http-equiv="Expires" content="<?php echo gmdate('D, d M Y H:i:s T', time() + $cacheTime); ?>">
</head>
<body>

    <h1><?php echo $content['title']; ?></h1>
    <p><?php echo $content['body']; ?></p>


</body>
</html>
<?php


// Cache the generated output
$cachedContent = ob_get_contents();
ob_end_clean(); 

// Create the cache directory if it doesn't exist
if (!is_dir($cacheDir)) {
    mkdir($cacheDir, 0755, true);  // Ensure proper permissions
}


file_put_contents($cacheFile, $cachedContent);

// Send the output (now cached)
echo $cachedContent;




// config/config.php (example) - store this securely outside the webroot
define('DB_HOST', 'your_db_host');
define('DB_NAME', 'your_db_name');
define('DB_USER', 'your_db_user');
define('DB_PASS', 'your_strong_db_password');

?>