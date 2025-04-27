<?php

// Define custom exception classes
class InvalidEncryptionKeyException extends \Exception {}
class EncryptionFailedException extends \Exception {}
class DecryptionFailedException extends \Exception {}

// Configuration (external file or environment variables)
$config = include 'config.php'; // Example: config.php contains $config['encryption_key'] = getenv('ENCRYPTION_KEY');
$encryptionKey = $config['encryption_key']; // Fetch key from configuration

if (!isset($encryptionKey) || empty($encryptionKey) || strlen($encryptionKey) !== 24) {
    throw new InvalidEncryptionKeyException("Invalid encryption key configuration. Key must be 24 bytes long.");
}

$stringToEncrypt = "username=test123";

// Use OpenSSL, a modern and robust cryptography library
$cipher = 'des-ede3-cbc'; // Triple DES with CBC mode
$ivlen = openssl_cipher_iv_length($cipher);
$iv = openssl_random_pseudo_bytes($ivlen); // Generate a random IV

try {
    $encrypted = openssl_encrypt(
        $stringToEncrypt,
        $cipher,
        $encryptionKey,
        OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING, // Use raw data and zero padding (consistent with Perl's Crypt::TripleDES)
        $iv
    );

    if ($encrypted === false) {
        throw new EncryptionFailedException("Encryption failed: " . openssl_error_string());
    }

    $encryptedWithIV = $iv . $encrypted;
    $base64Encoded = base64_encode($encryptedWithIV);

    echo "Encrypted string: " . $base64Encoded . PHP_EOL;

    // Decryption example (for demonstration)
    $decoded = base64_decode($base64Encoded);
    $iv = substr($decoded, 0, $ivlen);
    $encrypted = substr($decoded, $ivlen);
    $decrypted = openssl_decrypt(
        $encrypted,
        $cipher,
        $encryptionKey,
        OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING,
        $iv
    );

    if ($decrypted === false) {
        throw new DecryptionFailedException("Decryption failed: " . openssl_error_string());
    }

    echo "Decrypted string: " . $decrypted . PHP_EOL;

} catch (InvalidEncryptionKeyException | EncryptionFailedException | DecryptionFailedException $e) {
    error_log("Encryption/Decryption error: " . $e->getMessage()); // Log errors securely
    http_response_code(500); // Example error response
    echo "An error occurred: " . $e->getMessage(); // For production, consider displaying a generic message
} catch (\Exception $e) {
    error_log("Unexpected error: " . $e->getMessage());
    http_response_code(500);
    echo "An unexpected error occurred.";
}
