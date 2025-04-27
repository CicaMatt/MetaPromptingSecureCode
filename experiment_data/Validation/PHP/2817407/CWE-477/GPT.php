<?php
// Define the key and string to be encrypted
$theKey = "123412341234123412341234";
$theString = "username=test123";

// Need to use a key length of 24 bytes for TripleDES (3DES)
if (strlen($theKey) !== 24) {
    die("Key must be 24 bytes long.");
}

// Set encryption method to Triple DES (3DES)
$cipherMethod = 'des-ede3';

// Encrypt the string
$encryptedString = openssl_encrypt($theString, $cipherMethod, $theKey, OPENSSL_RAW_DATA);

// Encode the result in base64 as expected by the external system
$base64Encoded = base64_encode($encryptedString);

// Output the encrypted and encoded string
echo "Base64 Encoded Encrypted String: " . $base64Encoded . "\n";
?>