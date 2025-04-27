<?php

// Configuration (environment variables preferred, or a secure configuration file)
$encryptionKey = getenv('PAYMENT_PROCESSOR_ENCRYPTION_KEY'); // Fetch from environment variables
if ($encryptionKey === false) {
    // Fallback to configuration file if environment variable not set (less secure)
    $config = include 'config.php'; // config.php should return an array
    $encryptionKey = $config['payment_processor_encryption_key'];
}

// Input validation (example)
$username = filter_input(INPUT_POST, 'username', FILTER_SANITIZE_STRING);
if ($username === null || $username === false) {
    die("Invalid username provided."); 
}

$dataToEncrypt = "username=" . $username;


// Encryption using OpenSSL (recommended over mcrypt)
$cipher = 'des-ede3'; // Triple DES
$ivlen = openssl_cipher_iv_length($cipher);
$iv = openssl_random_pseudo_bytes($ivlen); // Random IV is crucial for security

$encrypted = openssl_encrypt($dataToEncrypt, $cipher, $encryptionKey, OPENSSL_RAW_DATA, $iv);
$encrypted = base64_encode($iv . $encrypted);


// Output (for demonstration - in real application, send to payment processor securely)
echo "Encrypted Token: " . $encrypted . "\n";




// Example of config.php
// <?php
// return [
//     'payment_processor_encryption_key' => 'VERY_STRONG_AND_RANDOMLY_GENERATED_KEY_32_BYTES_LONG', //  Must be 24 bytes for 3DES
// ];


// Example of setting the environment variable (Linux/macOS)
// export PAYMENT_PROCESSOR_ENCRYPTION_KEY='VERY_STRONG_AND_RANDOMLY_GENERATED_KEY_32_BYTES_LONG'




// Decryption (for testing/demonstration)
$decoded = base64_decode($encrypted);
$iv = substr($decoded, 0, $ivlen);
$encryptedData = substr($decoded, $ivlen);
$decrypted = openssl_decrypt($encryptedData, $cipher, $encryptionKey, OPENSSL_RAW_DATA, $iv);
echo "Decrypted: " . $decrypted . "\n";


?>