<?php

// Configuration (store these securely, e.g., environment variables or a key management system)
$passphrase = getenv('ENCRYPTION_PASSPHRASE'); // Retrieve from environment variable
$salt = random_bytes(16); // Generate a random salt for each encryption
$hashAlgorithm = 'sha256'; // Use a strong hashing algorithm
$passwordIterations = 10000; // Increase iterations for stronger key derivation
$initVector = random_bytes(16); // Generate a random IV for each encryption
$keySize = 256; // Use a strong key size

function encrypt($plaintext) {
    global $passphrase, $salt, $hashAlgorithm, $passwordIterations, $initVector, $keySize;

    // Key derivation using PBKDF2
    $key = hash_pbkdf2($hashAlgorithm, $passphrase, $salt, $passwordIterations, $keySize / 8, true);

    // Encryption using AES-256-CBC with OpenSSL
    $ciphertext = openssl_encrypt($plaintext, 'aes-256-cbc', $key, OPENSSL_RAW_DATA, $initVector);

    // Encode ciphertext, salt, and IV for transmission (e.g., Base64)
    $encodedCiphertext = base64_encode($ciphertext);
    $encodedSalt = base64_encode($salt);
    $encodedIv = base64_encode($initVector);


    return $encodedCiphertext . ':' . $encodedSalt . ':' . $encodedIv;  // Return a single string
}

function decrypt($encryptedData) {
    global $passphrase, $hashAlgorithm, $passwordIterations, $keySize;

    $parts = explode(':', $encryptedData);
    if(count($parts) !== 3) {
        throw new Exception("Invalid encrypted data format.");
    }

    $encodedCiphertext = $parts[0];
    $encodedSalt = $parts[1];
    $encodedIv = $parts[2];

    $ciphertext = base64_decode($encodedCiphertext);
    $salt = base64_decode($encodedSalt);
    $initVector = base64_decode($encodedIv);

    // Key derivation using PBKDF2
    $key = hash_pbkdf2($hashAlgorithm, $passphrase, $salt, $passwordIterations, $keySize / 8, true);

    // Decryption using AES-256-CBC with OpenSSL
    $plaintext = openssl_decrypt($ciphertext, 'aes-256-cbc', $key, OPENSSL_RAW_DATA, $initVector);


    if ($plaintext === false) {
        throw new Exception("Decryption failed."); // Specific exception
    }

    return $plaintext;
}


// Example usage:
try {
    $encrypted = encrypt("My secret message");
    echo "Encrypted: " . $encrypted . "\n";

    $decrypted = decrypt($encrypted);
    echo "Decrypted: " . $decrypted . "\n";

} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "\n"; 
}

?>