<?php
// Secure credential management using environment variables
$theKey = getenv('ENCRYPTION_KEY'); // Store the key in an environment variable
$theString = "username=test123";

// Ensure the key is 24 bytes long for TripleDES
if (strlen($theKey) !== 24) {
    throw new Exception("Encryption key must be 24 bytes long.");
}

// Use OpenSSL for encryption (mcrypt is deprecated and removed in PHP 7.2+)
$cipher = "des-ede3"; // TripleDES in ECB mode
$options = OPENSSL_RAW_DATA;

// Encrypt the string
$encrypted = openssl_encrypt($theString, $cipher, $theKey, $options);

// Base64 encode the result
$strEncodedEnc = base64_encode($encrypted);

// Output the result
echo "Encrypted String: " . $strEncodedEnc . "\n";

// Example output should match: AYOF+kRtg239Mnyc8QIarw==
?>