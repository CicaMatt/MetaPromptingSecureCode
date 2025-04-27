<?php

$plainText = "Text To Encrypt"; // The text to encrypt
$passPhrase = "passpharse";
$saltValue = "saltvalue";
$hashAlgorithm = "sha1";
$passwordIterations = 2;
$initVector = "1a2b3c4d5e6f7g8h";
$keySize = 256;

// Derive the key
$saltValueBytes = $saltValue; // No ASCII encoding needed in PHP, it's already bytes
$derivedKey = openssl_pbkdf2($passPhrase, $saltValueBytes, $keySize / 8, $passwordIterations, $hashAlgorithm);


// Encrypt the data
$cipher = "aes-256-cbc"; // Rijndael-256 == AES-256
$encrypted = openssl_encrypt($plainText, $cipher, $derivedKey, OPENSSL_RAW_DATA, $initVector);

// Convert to base64
$encryptedtext = base64_encode($encrypted);


echo "->encrypt = $encryptedtext<br />";


// --- Decryption (for testing) ---

$decrypted = openssl_decrypt(base64_decode($encryptedtext), $cipher, $derivedKey, OPENSSL_RAW_DATA, $initVector);

echo "->decrypt = $decrypted<br />";

?>