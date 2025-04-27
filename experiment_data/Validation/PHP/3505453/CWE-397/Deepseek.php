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
        $key = $passPhrase . $salt;
        for ($i = 0; $i < $iterations; $i++) {
            $key = hash($hashAlgorithm, $key, true);
        }
        return substr($key, 0, $keySize / 8);
    }

    function encrypt($input) {
        $paddedInput = $this->pkcs7_pad($input, 16); // Pad input to block size
        $encrypted = openssl_encrypt($paddedInput, 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
        return base64_encode($encrypted);
    }

    function decrypt($input) {
        $decrypted = openssl_decrypt(base64_decode($input), 'AES-256-CBC', $this->securekey, OPENSSL_RAW_DATA, $this->iv);
        return $this->pkcs7_unpad($decrypted);
    }

    private function pkcs7_pad($data, $size) {
        $length = $size - strlen($data) % $size;
        return $data . str_repeat(chr($length), $length);
    }

    private function pkcs7_unpad($data) {
        $length = ord($data[strlen($data) - 1]);
        return substr($data, 0, -$length);
    }
}

$cipher = new Cipher($HashPassPhrase, $HashSalt, $HashAlgorithm, $HashIterations, $InitVector, $keySize);

$encryptedtext = $cipher->encrypt("Text To Encrypt");
echo "->encrypt = $encryptedtext<br />";

$decryptedtext = $cipher->decrypt($encryptedtext);
echo "->decrypt = $decryptedtext<br />";

var_dump($cipher);