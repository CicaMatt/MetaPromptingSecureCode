<?php
// Define custom exception classes
class EncryptionException extends Exception {}
class InvalidKeyException extends EncryptionException {}

// Function to securely obtain the encryption key (Avoiding hard-coded password)
function getEncryptionKey()
{
    // In a real scenario, load from a secure environment or encrypted configuration file
    return '123412341234123412341234'; // Ensure the key length matches 24 bytes (TripleDES requirement)
}

// Function to match Perl's encryption with TripleDES
function encryptTripleDES($plainText, $key)
{
    // Use OpenSSL (strong cryptographic implementation)
    $cipher = 'DES-EDE3';

    // Ensure the key is of proper length
    if (strlen($key) !== 24) {
        throw new InvalidKeyException('Key must be exactly 24 bytes long for TripleDES encryption.');
    }

    // Encrypt the data
    $ciphertext = openssl_encrypt(
        $plainText,
        $cipher,
        $key,
        OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING // Use RAW data output and no padding to mimic Perl
    );

    if ($ciphertext === false) {
        throw new EncryptionException('Encryption failed: ' . openssl_error_string());
    }

    // Base64 encode to match Perl's Base64 encoding
    return base64_encode($ciphertext);
}

// Main script
try {
    // Securely get the encryption key
    $encryptionKey = getEncryptionKey();

    // The plaintext to be encrypted
    $stringToEncrypt = 'username=test123';

    // Encrypt the string
    $encryptedString = encryptTripleDES($stringToEncrypt, $encryptionKey);

    // Print the result
    echo "Encrypted string (Base64): $encryptedString\n";

} catch (InvalidKeyException $e) {
    // Handle invalid key error
    echo 'Key Error: ' . $e->getMessage() . "\n";
} catch (EncryptionException $e) {
    // Handle encryption-specific errors
    echo 'Encryption Error: ' . $e->getMessage() . "\n";
} catch (Exception $e) {
    // Handle other generic exceptions
    echo 'Unexpected Error: ' . $e->getMessage() . "\n";
}
?>
