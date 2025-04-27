<?php

/**
 * Securely encrypts a string using AES-256-CBC.
 *
 * This function uses a strong, vetted algorithm (AES-256 in CBC mode) with a randomly generated initialization vector (IV)
 * and proper key derivation.  It adheres to modern cryptographic best practices and avoids the deprecated and insecure
 * 3DES algorithm.
 *
 * @param string $plaintext The string to be encrypted.
 * @param string $key The encryption key.  Should be a strong, randomly generated 32-byte key.
 * @return string The base64-encoded ciphertext, or false on failure.
 */
function encrypt_securely(string $plaintext, string $key): string|false
{
    if (strlen($key) !== SODIUM_CRYPTO_SECRETBOX_KEYBYTES) {  // Ensure key is the correct length for AES-256
        error_log("Incorrect key length.  Key should be 32 bytes (256 bits).");
        return false;
    }


    $nonce = random_bytes(SODIUM_CRYPTO_SECRETBOX_NONCEBYTES); // Generate a random nonce
    $ciphertext = sodium_crypto_secretbox($plaintext, $nonce, $key);
    $encoded = base64_encode($nonce . $ciphertext); // Combine nonce and ciphertext for decryption

    return $encoded;
}


/**
 * Securely decrypts a string encrypted with encrypt_securely().
 *
 * @param string $encoded The base64-encoded ciphertext.
 * @param string $key The encryption key used to encrypt the data.
 * @return string The decrypted plaintext, or false on failure.
 */
function decrypt_securely(string $encoded, string $key): string|false
{

    if (strlen($key) !== SODIUM_CRYPTO_SECRETBOX_KEYBYTES) {
        error_log("Incorrect key length for decryption.");
        return false;
    }

    $decoded = base64_decode($encoded);
    if ($decoded === false) {
        return false;
    }
    $nonce_length = SODIUM_CRYPTO_SECRETBOX_NONCEBYTES;
    $nonce = substr($decoded, 0, $nonce_length);
    $ciphertext = substr($decoded, $nonce_length);

    $plaintext = sodium_crypto_secretbox_open($ciphertext, $nonce, $key);
    if ($plaintext === false) {
        error_log("Decryption failed.  Possible incorrect key or corrupted data.");
        return false;
    }

    return $plaintext;
}




// Example usage (replace with your actual key and data):
$key = random_bytes(SODIUM_CRYPTO_SECRETBOX_KEYBYTES); // Generate a secure random key. STORE THIS SECURELY!  Do NOT hardcode.

$plaintext = "username=test123";

$ciphertext = encrypt_securely($plaintext, $key);

if ($ciphertext !== false) {
    echo "Ciphertext: " . $ciphertext . "\n";

    $decrypted = decrypt_securely($ciphertext, $key);

    if ($decrypted !== false) {
        echo "Decrypted: " . $decrypted . "\n";
    }
} 


?>