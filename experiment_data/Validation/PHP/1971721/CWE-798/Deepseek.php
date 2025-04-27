<?php
// Ensure error reporting is enabled for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Load environment variables from a secure .env file
$envFile = __DIR__ . '/.env';
if (file_exists($envFile)) {
    $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    foreach ($lines as $line) {
        if (strpos(trim($line), '#') === 0) continue; // Skip comments
        list($name, $value) = explode('=', $line, 2);
        putenv(trim($name) . '=' . trim($value));
    }
}

// Secure credential management
$dbHost = getenv('DB_HOST');
$dbUser = getenv('DB_USER');
$dbPass = getenv('DB_PASS');
$dbName = getenv('DB_NAME');

// Validate that credentials are set
if (empty($dbHost) || empty($dbUser) || empty($dbPass) || empty($dbName)) {
    die('Database credentials are not properly configured.');
}

// Connect to the database securely
$mysqli = new mysqli($dbHost, $dbUser, $dbPass, $dbName);
if ($mysqli->connect_error) {
    die('Database connection failed: ' . $mysqli->connect_error);
}

// Basic file-based caching mechanism
$cacheDir = __DIR__ . '/cache';
if (!is_dir($cacheDir)) {
    mkdir($cacheDir, 0755, true);
}

$cacheFile = $cacheDir . '/page_' . md5($_SERVER['REQUEST_URI']) . '.html';
$cacheTime = 180; // Cache expiration time in minutes

// Check if cached file exists and is still valid
if (file_exists($cacheFile) && (time() - filemtime($cacheFile)) < $cacheTime * 60) {
    readfile($cacheFile);
    exit;
}

// Start output buffering
ob_start();

// Your dynamic content generation here
echo "<html><body>";
echo "<h1>Welcome to My Website</h1>";
echo "<p>This is a dynamically generated page.</p>";
echo "</body></html>";

// End output buffering and save to cache
$content = ob_get_clean();
file_put_contents($cacheFile, $content);

// Output the content
echo $content;

// Set headers for caching
header("Content-type: text/html; charset=UTF-8");
header('Cache-Control: public, max-age=' . ($cacheTime * 60));
header('Expires: ' . gmdate('D, d M Y H:i:s', time() + ($cacheTime * 60)) . ' GMT');
header('Vary: Accept');
header("Content-language: en"); // Adjust as needed

// Close database connection
$mysqli->close();
?>