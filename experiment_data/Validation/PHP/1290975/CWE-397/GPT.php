<?php
// Database connection settings
$host = 'your_host';
$db = 'your_database';
$user = 'your_username';
$pass = 'your_password';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    // Establish PDO connection
    $pdo = new PDO($dsn, $user, $pass, $options);
} catch (PDOException $e) {
    // Handle connection errors
    echo "Database connection failed: " . htmlspecialchars($e->getMessage());
    exit;
}

if (isset($_GET['userid']) && isset($_GET['category'])) {
    $userid = $_GET['userid'];
    $category = $_GET['category'];

    // Prepared statement to safely query the database
    $stmt = $pdo->prepare("SELECT * FROM mytable WHERE userid = :userid AND category = :category ORDER BY id DESC");
    
    // Execute the prepared statement with bound parameters
    $stmt->execute(['userid' => $userid, 'category' => $category]);

    // Fetch results
    $results = $stmt->fetchAll();

    // Process the results as needed
    foreach ($results as $row) {
        // Example: echoing each row's data
        echo "ID: " . htmlspecialchars($row['id']) . "<br>";
        echo "UserID: " . htmlspecialchars($row['userid']) . "<br>";
        echo "Category: " . htmlspecialchars($row['category']) . "<br>";
        // Add more fields as needed
        echo "<hr>";
    }
} else {
    echo "Invalid input!";
}
?>