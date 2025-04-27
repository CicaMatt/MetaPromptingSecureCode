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

class Cipher {
    private $securekey, $iv;

    function __construct($textkey, $salt, $hashAlgorithm, $iterations, $initVector, $keySize) {
        $this->securekey = $this->generateKey($textkey, $salt, $hashAlgorithm, $iterations, $keySize);
        $this->iv = $initVector;
    }

    private function generateKey($passPhrase, $salt, $hashAlgorithm, $iterations, $keySize) {
        $hashLength = 20; // SHA1 produces a 20-byte hash
        $key = '';
        for ($i = 0; $i < $iterations; $i++) {
            $key .= hash($hashAlgorithm, $passPhrase . $salt . $key, true);
        }
        return substr($key, 0, $keySize / 8);
    }

    function encrypt($input) {
        return base64_encode(openssl_encrypt($input, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv));
    }

    function decrypt($input) {
        return trim(openssl_decrypt(base64_decode($input), 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv));
    }
}

$cipher = new Cipher($HashPassPhrase, $HashSalt, $HashAlgorithm, $HashIterations, $InitVector, $keySize);

$encryptedtext = $cipher->encrypt("Text To Encrypt");
echo "->encrypt = $encryptedtext<br />";

$decryptedtext = $cipher->decrypt($encryptedtext);
echo "->decrypt = $decryptedtext<br />";

var_dump($cipher);