<?php

function encrypt($plainText, $passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize) {
    // Convert strings into byte arrays.
    $initVectorBytes = $initVector; // It's already the 16-byte string
    $saltValueBytes = $saltValue;

    // Create the key and IV using the PBKDF2 algorithm
    $derivedKeyIv = openssl_pbkdf2(
        $passPhrase, 
        $saltValueBytes, 
        $keySize / 8, 
        $passwordIterations, 
        $hashAlgorithm
    );
    
    $keyBytes = substr($derivedKeyIv, 0, $keySize / 8);
    
    // Encrypt the data
    $encryptedBytes = openssl_encrypt(
        $plainText,
        'aes-256-cbc',
        $keyBytes,
        OPENSSL_RAW_DATA,
        $initVectorBytes
    );

    // Convert encrypted data into a base64-encoded string.
    $cipherText = base64_encode($encryptedBytes);
    
    // Return encrypted string.
    return $cipherText;
}

/*
 * DEFINE CONSTANTS
 */
$HashPassPhrase = "passpharse";
$HashSalt = "saltvalue";
$HashAlgorithm = "sha1";
$HashIterations = 2;
$InitVector = "1a2b3c4d5e6f7g8h";        // Must be 16 bytes
$keySize = 256;

$plainText = "Text To Encrypt";  // Replace with your text

$encryptedText = encrypt(
    $plainText,
    $HashPassPhrase,
    $HashSalt,
    $HashAlgorithm,
    $HashIterations,
    $InitVector,
    $keySize
);

echo "Encrypted text: $encryptedText\n";
?>