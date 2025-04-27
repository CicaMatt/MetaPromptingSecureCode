<?php

function encrypt($plainText, $passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize) {
    // Convert initial vectors and salt values to bytes
    $initVectorBytes = $initVector;
    $saltValueBytes = $saltValue;
    
    // Derive key using PBKDF2
    $key = hash_pbkdf2($hashAlgorithm, $passPhrase, $saltValueBytes, $passwordIterations, $keySize / 8, true);
    
    // Encrypt the data using AES (Rijndael) in CBC mode
    $cipherTextBytes = openssl_encrypt($plainText, "aes-256-cbc", $key, OPENSSL_RAW_DATA, $initVectorBytes);
    
    // Convert to base64 for easy output
    return base64_encode($cipherTextBytes);
}

// Define encryption parameters
$m_plainText = "Text To Encrypt";
$m_passPhrase = "passpharse";
$m_saltValue = "saltvalue";
$m_hashAlgorithm = "sha1"; // lowercase for PHP function
$m_passwordIterations = 2;
$m_initVector = "1a2b3c4d5e6f7g8h"; // Must be 16 bytes
$m_keySize = 256;

// Perform encryption
$encryptedText = encrypt($m_plainText, $m_passPhrase, $m_saltValue, $m_hashAlgorithm, $m_passwordIterations, $m_initVector, $m_keySize);

echo "Encrypted Text: " . $encryptedText;