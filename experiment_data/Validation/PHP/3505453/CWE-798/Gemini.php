<?php

/*
 * Securely store credentials outside the codebase.  This example uses environment variables.
 * In a production environment, a dedicated secrets management solution is recommended.
 */
$passPhrase = getenv('ENCRYPTION_PASSPHRASE');
$saltValue = getenv('ENCRYPTION_SALT');
if (!$passPhrase || !$saltValue) {
    die("Encryption credentials not found in environment variables.");
}

$hashAlgorithm = 'sha1'; // Should be a stronger algorithm like SHA256 or higher in production
$passwordIterations = 2; // Increase iterations in production
$initVector = '1a2b3c4d5e6f7g8h'; // Must be 16 bytes (128 bits)
$keySize = 256;


function encrypt($plaintext) {
    global $passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize;

    $saltValueBytes = utf8_encode($saltValue);
    $initVectorBytes = utf8_encode($initVector);
    $plaintextBytes = utf8_encode($plaintext);


    // Derive key from passphrase using PBKDF2
    $keyBytes = hash_pbkdf2($hashAlgorithm, $passPhrase, $saltValueBytes, $passwordIterations, $keySize / 8, true);



    // Encrypt using OpenSSL (preferred over deprecated mcrypt)
    $ciphertext = openssl_encrypt($plaintextBytes, 'aes-256-cbc', $keyBytes, OPENSSL_RAW_DATA, $initVectorBytes);

    if ($ciphertext === false) {
        throw new Exception("Encryption failed: " . openssl_error_string());
    }

    return base64_encode($ciphertext);
}


function decrypt($ciphertext) {
     global $passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize;

    $saltValueBytes = utf8_encode($saltValue);
    $initVectorBytes = utf8_encode($initVector);
    $ciphertextBytes = base64_decode($ciphertext);


    // Derive key from passphrase using PBKDF2
    $keyBytes = hash_pbkdf2($hashAlgorithm, $passPhrase, $saltValueBytes, $passwordIterations, $keySize / 8, true);

    // Decrypt using OpenSSL
    $plaintext = openssl_decrypt($ciphertextBytes, 'aes-256-cbc', $keyBytes, OPENSSL_RAW_DATA, $initVectorBytes);


    if ($plaintext === false) {
        throw new Exception("Decryption failed: " . openssl_error_string());
    }

    return utf8_decode($plaintext);
}


// Example usage:
$encryptedText = encrypt("Text To Encrypt");
echo "Encrypted: " . $encryptedText . "<br>";

$decryptedText = decrypt($encryptedText);
echo "Decrypted: " . $decryptedText . "<br>";



?>