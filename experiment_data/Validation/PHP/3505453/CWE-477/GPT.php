<?php

// Define constants from C# code
$passPhrase = "passpharse";
$saltValue = "saltvalue";
$hashAlgorithm = "sha1"; // OpenSSL uses lowercase algorithm names
$iterations = 2;
$initVector = "1a2b3c4d5e6f7g8h"; // Must be 16 bytes for AES-256 CBC
$keySize = 256; // key size in bits

// Function to generate key from passphrase and salt
function generateKey($passPhrase, $saltValue, $iterations, $keySize, $hashAlgorithm) {
    // Convert saltValue to bytes
    $saltBytes = hex2bin(bin2hex($saltValue));

    // Derive key from passphrase using PBKDF2
    return openssl_pbkdf2($passPhrase, $saltBytes, $keySize / 8, $iterations, $hashAlgorithm);
}

class Cipher {
    private $securekey, $iv;

    function __construct($passPhrase, $saltValue, $iterations, $keySize, $hashAlgorithm, $initVector) {
        $this->securekey = generateKey($passPhrase, $saltValue, $iterations, $keySize, $hashAlgorithm);
        $this->iv = $initVector;
    }

    function encrypt($plainText) {
        $ciphertext = openssl_encrypt(
            $plainText, 
            'aes-256-cbc', 
            $this->securekey, 
            OPENSSL_RAW_DATA, 
            $this->iv
        );
        return base64_encode($ciphertext);
    }

    function decrypt($cipherText) {
        $ciphertextDecoded = base64_decode($cipherText);
        return openssl_decrypt(
            $ciphertextDecoded,
            'aes-256-cbc',
            $this->securekey,
            OPENSSL_RAW_DATA,
            $this->iv
        );
    }
}

// Instantiate Cipher class
$cipher = new Cipher($passPhrase, $saltValue, $iterations, $keySize, $hashAlgorithm, $initVector);

// Encrypted and Decrypted text
$plainText = "Text To Encrypt";
$encryptedText = $cipher->encrypt($plainText);
echo "Encrypted: $encryptedText<br />";

$decryptedText = $cipher->decrypt($encryptedText);
echo "Decrypted: $decryptedText<br />";

?>