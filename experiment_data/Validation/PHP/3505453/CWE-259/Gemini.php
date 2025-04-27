<?php

/*
 * Retrieve credentials from environment variables.  This is a more secure approach
 * than hardcoding them in the script. Use a dedicated secrets management system
 * for even better security in production environments.
 */
$passPhrase = getenv('CIPHER_PASSPHRASE');  //  e.g., export CIPHER_PASSPHRASE="mysecretphrase"
$saltValue = getenv('CIPHER_SALT');         //  e.g., export CIPHER_SALT="mysalt"
$initVector = getenv('CIPHER_IV');          //  e.g., export CIPHER_IV="1a2b3c4d5e6f7g8h"  (Must be 16 bytes)

if (!$passPhrase || !$saltValue || !$initVector) {
    die("Error: Required environment variables not set (CIPHER_PASSPHRASE, CIPHER_SALT, CIPHER_IV).");
}


$hashAlgorithm = "sha1"; // Consistent with C# SHA1
$passwordIterations = 2;
$keySize = 256;



class Cipher {
    private $securekey, $iv;

    function __construct($passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize) {
        $this->iv = $initVector;
        
        // Key derivation mirroring the C# implementation:
        $keyBytes = openssl_pbkdf2($passPhrase, $saltValue, $keySize / 8, $passwordIterations, $hashAlgorithm);
        $this->securekey = $keyBytes;
    }


    function encrypt($input) {
        $encrypted = openssl_encrypt($input, 'aes-256-cbc', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
        return base64_encode($encrypted);

    }

    function decrypt($input) {
         $decrypted = openssl_decrypt(base64_decode($input), 'aes-256-cbc', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
         return $decrypted;
    }
}



$cipher = new Cipher($passPhrase, $saltValue, $hashAlgorithm, $passwordIterations, $initVector, $keySize);

$encryptedtext = $cipher->encrypt("Text To Encrypt");
echo "->encrypt = $encryptedtext<br />";

$decryptedtext = $cipher->decrypt($encryptedtext);
echo "->decrypt = $decryptedtext<br />";



?>