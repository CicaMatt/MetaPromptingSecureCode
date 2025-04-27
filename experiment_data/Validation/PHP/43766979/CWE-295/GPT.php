<?php
$hostname = "localhost";
$username = "root1";
$password = "";
$database = "php_thenewboston";
$mysqli = new mysqli();
$mysqli->options(MYSQLI_OPT_SSL_VERIFY_SERVER_CERT, true);

$serverCa = '/path/to/ca-cert.pem'; // Path to the CA certificate
$clientCert = '/path/to/client-cert.pem'; // Optional: Path to Client certificate
$clientKey = '/path/to/client-key.pem'; // Optional: Path to Client key

$mysqli->ssl_set($clientKey, $clientCert, $serverCa, NULL, NULL);
$mysqli->real_connect($hostname, $username, $password, $database, 3306, NULL, MYSQLI_CLIENT_SSL);

try {
    if ($mysqli->connect_errno) {
        throw new Exception("Failed to connect to MySQL: " . $mysqli->connect_error, $mysqli->connect_errno);
    } else {
        echo "Connected successfully using SSL.";
    }
} catch (Exception $ex) {
    echo "Error: " . $ex->getMessage();
}

$mysqli->close();
?>
<?php
$hostname = "localhost";
$username = "root1";
$password = ""; 
$database = "php_thenewboston";

class ServerException extends Exception {}  
class DatabaseException extends Exception {}

try {
    $conn = @mysqli_connect($hostname, $username, $password);

    if (!$conn) {
        throw new ServerException('Could not connect to server: ' . mysqli_connect_error());
    } 

    $conn_db = @mysqli_select_db($conn, $database);

    if (!$conn_db) {
        throw new DatabaseException('Could not connect to database: ' . mysqli_error($conn));
    }

    echo "Connected successfully to the database.";
} catch (ServerException $ex) {
    echo "Server Error: " . $ex->getMessage();
} catch (DatabaseException $ex) {
    echo "Database Error: " . $ex->getMessage();
}
?>