<?php

/*
 * DEFINE CONSTANTS
 */
$HashPassPhrase = "passpharse";
$HashSalt = "saltvalue";
$HashAlgorithm = "SHA1";
$HashIterations = 2;
$InitVector = "1a2b3c4d5e6f7g8h";        // Must be 16 bytes
$keySize = 256;

function deriveKey($passPhrase, $saltValue, $hashAlgorithm, $iterations, $keySize) {
    $key = $passPhrase . $saltValue;
    for ($i = 0; $i < $iterations; $i++) {
        $key = hash($hashAlgorithm, $key, true);
    }
    return substr($key, 0, $keySize / 8);
}

function encrypt($plainText, $passPhrase, $saltValue, $hashAlgorithm, $iterations, $initVector, $keySize) {
    // Convert strings into byte arrays.
    $initVectorBytes = $initVector;
    $saltValueBytes = $saltValue;

    // Convert our plaintext into a byte array.
    $plainTextBytes = $plainText;

    // Derive the key
    $keyBytes = deriveKey($passPhrase, $saltValueBytes, $hashAlgorithm, $iterations, $keySize);

    // Create encryptor
    $cipher = openssl_encrypt(
        $plainTextBytes,
        'AES-256-CBC',
        $keyBytes,
        OPENSSL_RAW_DATA,
        $initVectorBytes
    );

    // Convert encrypted data into a base64-encoded string.
    $cipherText = base64_encode($cipher);

    // Return encrypted string.
    return $cipherText;
}

function decrypt($cipherText, $passPhrase, $saltValue, $hashAlgorithm, $iterations, $initVector, $keySize) {
    // Convert strings into byte arrays.
    $initVectorBytes = $initVector;
    $saltValueBytes = $saltValue;

    // Derive the key
    $keyBytes = deriveKey($passPhrase, $saltValueBytes, $hashAlgorithm, $iterations, $keySize);

    // Decrypt the data
    $plainTextBytes = openssl_decrypt(
        base64_decode($cipherText),
        'AES-256-CBC',
        $keyBytes,
        OPENSSL_RAW_DATA,
        $initVectorBytes
    );

    // Return decrypted string.
    return $plainTextBytes;
}

// Example usage
$plainText = "Text To Encrypt";
$encryptedText = encrypt($plainText, $HashPassPhrase, $HashSalt, $HashAlgorithm, $HashIterations, $InitVector, $keySize);
echo "Encrypted: $encryptedText\n";

$decryptedText = decrypt($encryptedText, $HashPassPhrase, $HashSalt, $HashAlgorithm, $HashIterations, $InitVector, $keySize);
echo "Decrypted: $decryptedText\n";
?>