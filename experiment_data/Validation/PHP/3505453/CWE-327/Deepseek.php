<?php

/*
 * DEFINE CONSTANTS
 */
$HashPassPhrase = "passpharse";
$HashSalt = "saltvalue";
$HashAlgorithm = "SHA1";
$HashIterations = 2;
$InitVector = "1a2b3c4d5e6f7g8h";  // Must be 16 bytes
$keySize = 256;

class Cipher {
    private $securekey, $iv;

    function __construct($passPhrase, $salt, $hashAlgorithm, $iterations, $initVector, $keySize) {
        // Generate the key using PBKDF2 with the specified parameters
        $this->securekey = hash_pbkdf2($hashAlgorithm, $passPhrase, $salt, $iterations, $keySize / 8, true);
        $this->iv = $initVector;
    }

    function encrypt($input) {
        // Encrypt the input using AES-256-CBC
        $cipherText = openssl_encrypt($input, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
        // Return the base64-encoded cipher text
        return base64_encode($cipherText);
    }

    function decrypt($input) {
        // Decode the base64-encoded input
        $cipherText = base64_decode($input);
        // Decrypt the input using AES-256-CBC
        $plainText = openssl_decrypt($cipherText, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
        // Return the decrypted text
        return trim($plainText);
    }
}

// Create a new Cipher instance with the specified parameters
$cipher = new Cipher($HashPassPhrase, $HashSalt, $HashAlgorithm, $HashIterations, $InitVector, $keySize);

// Encrypt the text
$encryptedtext = $cipher->encrypt("Text To Encrypt");
echo "->encrypt = $encryptedtext<br />";

// Decrypt the text
$decryptedtext = $cipher->decrypt($encryptedtext);
echo "->decrypt = $decryptedtext<br />";

?>