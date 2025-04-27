<?php

/*
 * DEFINE CONSTANTS
 */
define('HASH_PASS_PHRASE', getenv('ENCRYPTION_PASS_PHRASE') ?: 'passpharse'); // Use environment variable for passphrase
define('HASH_SALT', getenv('ENCRYPTION_SALT') ?: 'saltvalue'); // Use environment variable for salt
define('HASH_ALGORITHM', 'SHA256'); // Use SHA-256 instead of SHA-1
define('HASH_ITERATIONS', 10000); // Increase iterations for better security
define('INIT_VECTOR', '1a2b3c4d5e6f7g8h'); // Must be 16 bytes
define('KEY_SIZE', 256); // Key size in bits

class Cipher {
    private $securekey, $iv;

    function __construct($textkey, $salt) {
        // Derive key using PBKDF2 with HMAC-SHA256
        $this->securekey = hash_pbkdf2(HASH_ALGORITHM, $textkey, $salt, HASH_ITERATIONS, KEY_SIZE / 8, true);
        $this->iv = INIT_VECTOR;
    }

    function encrypt($input) {
        // Use OpenSSL for encryption (AES-256-CBC)
        $cipherText = openssl_encrypt($input, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
        return base64_encode($cipherText);
    }

    function decrypt($input) {
        // Use OpenSSL for decryption (AES-256-CBC)
        $cipherText = base64_decode($input);
        return openssl_decrypt($cipherText, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
    }
}

// Create cipher instance
$cipher = new Cipher(HASH_PASS_PHRASE, HASH_SALT);

// Encrypt and decrypt example
$plainText = "Text To Encrypt";
$encryptedText = $cipher->encrypt($plainText);
echo "->encrypt = $encryptedText<br />";

$decryptedText = $cipher->decrypt($encryptedText);
echo "->decrypt = $decryptedText<br />";

// Verify decryption
if ($plainText === $decryptedText) {
    echo "Encryption and decryption successful!";
} else {
    echo "Encryption and decryption failed!";
}