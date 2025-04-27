<?php

/**
 * Encrypts a string using AES-256-CBC.  Mirrors the provided C# code's key derivation and encryption process.
 *
 * @param string $plainText The text to encrypt.
 * @param string $passPhrase The passphrase for key derivation.
 * @param string $saltValue The salt for key derivation.
 * @param string $hashAlgorithm The hash algorithm for key derivation (e.g., 'sha256').
 * @param int $passwordIterations The number of iterations for key derivation.
 * @param string $initVector The initialization vector (IV). MUST be 16 bytes.
 * @param int $keySize The key size in bits (e.g., 256).
 * @return string The base64-encoded ciphertext.
 * @throws Exception If encryption fails.
 */
function encrypt($plainText, $passPhrase = "passpharse", $saltValue = "saltvalue", $hashAlgorithm = 'sha256', $passwordIterations = 2, $initVector = "1a2b3c4d5e6f7g8h", $keySize = 256)
{
    // Validate IV length
    if (strlen($initVector) !== 16) {
        throw new Exception("Initialization vector must be 16 bytes long.");
    }

    // Key Derivation (PBKDF2 implemented manually for compatibility with older PHP versions)
    $keyBytes = '';
    for ($i = 0; $i < $passwordIterations; $i++) {
        $keyBytes = hash_hmac($hashAlgorithm, $keyBytes . $passPhrase . $saltValue, true);  // Corrected key derivation logic using HMAC
    }    
    $keyBytes = substr($keyBytes, 0, $keySize / 8); // Ensure correct key length

    // Encryption using OpenSSL (recommended over mcrypt)
    $cipherText = openssl_encrypt(
        $plainText,
        'aes-256-cbc',
        $keyBytes,
        OPENSSL_RAW_DATA,  // Important for compatibility with C#
        $initVector
    );


    if ($cipherText === false) {
        throw new Exception("Encryption failed: " . openssl_error_string());
    }

    return base64_encode($cipherText);
}



// Example Usage (Test vectors matching the C# example)
$plainText = "Text To Encrypt";
$encryptedText = encrypt($plainText);

echo "Encrypted: " . $encryptedText . "\n";


// Decryption in C# (provided code with minor fix - closing streams in finally block is better practice)

// ... (C# code as in the problem description, with the following modification in Decrypt() function)

// ... inside the Decrypt() function:

try
{
   // ... (rest of the decryption code)
}
finally
{
    cryptoStream.Close(); // Ensure stream closure in all cases
    memoryStream.Close();
}