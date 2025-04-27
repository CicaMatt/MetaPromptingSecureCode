<?php

/*
 * DEFINE CONSTANTS FROM ENVIRONMENT VARIABLES
 */
$HashPassPhrase = getenv('ENCRYPTION_PASSPHRASE'); // Use environment variable for passphrase
$HashSalt = getenv('ENCRYPTION_SALT'); // Use environment variable for salt
$HashAlgorithm = "SHA1";
$HashIterations = 2;
$InitVector = "1a2b3c4d5e6f7g8h"; // Must be 16 bytes
$keySize = 256;

class Cipher {
    private $securekey, $iv;

    function __construct($textkey, $salt, $hashAlgorithm, $iterations, $initVector, $keySize) {
        // Derive the key using PBKDF2 with HMAC-SHA1
        $this->securekey = hash_pbkdf2($hashAlgorithm, $textkey, $salt, $iterations, $keySize / 8, true);
        $this->iv = $initVector;
    }

    function encrypt($input) {
        // Pad the input to be a multiple of the block size
        $blockSize = 32; // 256 bits = 32 bytes
        $padding = $blockSize - (strlen($input)) % $blockSize;
        $input .= str_repeat(chr($padding), $padding);

        // Encrypt the input using AES-256-CBC
        $cipherText = openssl_encrypt($input, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);

        // Return base64 encoded cipher text
        return base64_encode($cipherText);
    }

    function decrypt($input) {
        // Decode the base64 encoded input
        $cipherText = base64_decode($input);

        // Decrypt the input using AES-256-CBC
        $plainText = openssl_decrypt($cipherText, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);

        // Remove padding
        $padding = ord($plainText[strlen($plainText) - 1]);
        $plainText = substr($plainText, 0, -$padding);

        return $plainText;
    }
}

// Create a new Cipher instance with the passphrase, salt, hash algorithm, iterations, init vector, and key size
$cipher = new Cipher($HashPassPhrase, $HashSalt, $HashAlgorithm, $HashIterations, $InitVector, $keySize);

// Example usage
$plainText = "Text To Encrypt";
$encryptedText = $cipher->encrypt($plainText);
echo "Encrypted: $encryptedText\n";

$decryptedText = $cipher->decrypt($encryptedText);
echo "Decrypted: $decryptedText\n";