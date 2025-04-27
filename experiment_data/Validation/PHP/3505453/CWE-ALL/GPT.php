<?php

// Securely load the configuration settings
// These would typically be read from a secure storage or environment variables
$passPhrase = getenv('ENCRYPTION_PASSPHRASE');
$saltValue = getenv('ENCRYPTION_SALTVALUE');
$hashAlgorithm = getenv('ENCRYPTION_HASH_ALGORITHM') ?: 'sha256';
$passwordIterations = 2;
$initVector = '1a2b3c4d5e6f7g8h';
$keySize = 256;

class SecureCipher {
    private $key;
    private $iv;

    public function __construct($passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize) {
        // Derive the key using PBKDF2, which is robust and secures against brute-force attacks
        $salt = mb_convert_encoding($saltValue, 'UTF-8');
        $key = hash_pbkdf2($hashAlgorithm, $passPhrase, $salt, $passwordIterations, $keySize / 8, true);

        $this->key = $key;
        $this->iv = $initVector;
    }

    public function encrypt($plainText) {
        // Encrypt using OpenSSL with AES-256-CBC
        $encrypted = openssl_encrypt($plainText, 'aes-256-cbc', $this->key, OPENSSL_RAW_DATA, $this->iv);
        return base64_encode($encrypted);
    }

    public function decrypt($cipherText) {
        $decoded = base64_decode($cipherText);
        return openssl_decrypt($decoded, 'aes-256-cbc', $this->key, OPENSSL_RAW_DATA, $this->iv);
    }
}

try {
    // Make sure to replace 'Text To Encrypt' with the actual text you want to encrypt
    $cipher = new SecureCipher($passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize);
    $encryptedText = $cipher->encrypt("Text To Encrypt");
    echo "Encrypted: $encryptedText\n";

    $decryptedText = $cipher->decrypt($encryptedText);
    echo "Decrypted: $decryptedText\n";
} catch (Exception $e) {
    // Handle specific exceptions related to encryption/decryption
    error_log('Encryption/Decryption error: ' . $e->getMessage());
    echo "An error occurred during encryption/decryption.\n";
}

?>