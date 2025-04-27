<?php

class EncryptionService {

    /**
     * Encrypts a string using TripleDES.
     *
     * @param string $data The string to encrypt.
     * @param string $key The encryption key.
     * @return string The encrypted string.
     * @throws InvalidArgumentException If the key or data is invalid.
     * @throws EncryptionException If the encryption process fails.
     */
    public function encrypt(string $data, string $key): string {
        if (strlen($key) !== 24) { // Check key length for 3DES
            throw new InvalidArgumentException("Invalid key length. Key must be 24 bytes.");
        }
        if (empty($data)) {
            throw new InvalidArgumentException("Data cannot be empty.");
        }


        try {
            $cipher = "des-ede3-cbc"; //Specify cipher mode explicitly
            $ivlen = openssl_cipher_iv_length($cipher);
            $iv = openssl_random_pseudo_bytes($ivlen); 
            $encrypted = openssl_encrypt($data, $cipher, $key, OPENSSL_RAW_DATA, $iv);


            if ($encrypted === false) {
                throw new EncryptionException("Encryption failed: " . openssl_error_string());
            }
            return base64_encode($iv . $encrypted);  // Return IV + ciphertext


        } catch (Exception $e) {  // Catch generic exception for unexpected errors
            // Log the exception for debugging
            error_log("An unexpected error occurred during encryption: " . $e->getMessage());
            // Re-throw as a more specific exception if possible, or handle appropriately
            throw new EncryptionException("Encryption process failed.", 0, $e); 
        }
    }
}


class EncryptionException extends Exception {}
class InvalidArgumentException extends Exception {}



// Example usage
$encryptionService = new EncryptionService();

try {
    $key = "123412341234123412341234";
    $data = "username=test123";
    $encryptedData = $encryptionService->encrypt($data, $key);
    echo "Encrypted data: " . $encryptedData . "\n";

    // Demonstrate decryption (ensure consistency)
    $decrypted = openssl_decrypt(substr(base64_decode($encryptedData),8), $cipher, $key, OPENSSL_RAW_DATA, substr(base64_decode($encryptedData),0,8));

    if ($decrypted === false) {
        throw new Exception("Decryption failed: " . openssl_error_string());
    }

    echo "Decrypted data: " . $decrypted . "\n";



} catch (InvalidArgumentException $e) {
    echo "Invalid input: " . $e->getMessage() . "\n";
} catch (EncryptionException $e) {
    echo "Encryption error: " . $e->getMessage() . "\n";
} catch (Exception $e) { // Catch any other unhandled exception
    echo "An unexpected error occurred: " . $e->getMessage() . "\n";
}


?>