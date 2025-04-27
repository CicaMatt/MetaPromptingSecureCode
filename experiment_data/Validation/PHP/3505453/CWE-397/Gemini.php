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

    /**
     * @param string $textkey
     * @throws Exception if hashing fails
     */
    function __construct($textkey) {
        $this->securekey = hash($HashAlgorithm, $textkey, true);
        if ($this->securekey === false) {
            throw new Exception("Hashing failed.");
        }
        $this->iv = $InitVector;
        if (strlen($this->iv) != 16) { // Explicitly check IV length
            throw new Exception("Initialization Vector must be 16 bytes.");
        }
    }


    /**
     * @param string $input
     * @return string
     * @throws Exception if encryption fails
     */
    function encrypt($input) {
        $encrypted = openssl_encrypt($input, 'aes-256-cbc', $this->securekey, 0, $this->iv);
        if ($encrypted === false) {
            throw new Exception("Encryption failed: " . openssl_error_string());
        }
        return base64_encode($encrypted);
    }

    /**
     * @param string $input
     * @return string
     * @throws Exception if decryption fails
     */
    function decrypt($input) {
        $decrypted = openssl_decrypt(base64_decode($input), 'aes-256-cbc', $this->securekey, 0, $this->iv);

        if ($decrypted === false) {
            throw new Exception("Decryption failed: " . openssl_error_string());
        }
        return $decrypted;
    }
}



try {
    $cipher = new Cipher($HashPassPhrase);
    $encryptedtext = $cipher->encrypt("Text To Encrypt");
    echo "->encrypt = $encryptedtext<br />";
    $decryptedtext = $cipher->decrypt($encryptedtext);
    echo "->decrypt = $decryptedtext<br />";


} catch (Exception $e) {
    echo "Error: " . $e->getMessage() . "<br />";
    //  Further error handling, like logging, could be done here.
}



?>