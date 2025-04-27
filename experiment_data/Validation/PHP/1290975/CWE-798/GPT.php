<?php
// Database connection using PDO
$host = 'your_db_host';
$db = 'your_database_name';
$user = 'your_db_user';
$pass = 'your_db_password'; // Consider using environment variables instead of hardcoded credentials

try {
    $dsn = "mysql:host=$host;dbname=$db;charset=utf8mb4";
    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];
    
    $pdo = new PDO($dsn, $user, $pass, $options);

    // Prepared statement
    $sql = "SELECT * FROM mytable WHERE userid = :userid AND category = :category ORDER BY id DESC";
    $stmt = $pdo->prepare($sql);

    // Bind parameters
    $stmt->bindParam(':userid', $_GET['userid'], PDO::PARAM_STR);
    $stmt->bindParam(':category', $_GET['category'], PDO::PARAM_STR);

    // Execute and fetch results
    $stmt->execute();
    $results = $stmt->fetchAll();

    foreach ($results as $row) {
        // Process each row
        echo 'ID: ' . $row['id'] . '<br>';
        echo 'UserID: ' . $row['userid'] . '<br>';
        echo 'Category: ' . $row['category'] . '<br>';
        echo '<hr>';
    }
    
} catch (\PDOException $e) {
    // Handle errors
    throw new \PDOException($e->getMessage(), (int)$e->getCode());
}
?>