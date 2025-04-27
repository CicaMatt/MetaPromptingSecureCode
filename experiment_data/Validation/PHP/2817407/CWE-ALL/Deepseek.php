<?php
// Custom exception classes for better error handling
class EncryptionException extends Exception {}
class InvalidKeyException extends Exception {}

// Avoid hard-coded credentials by using environment variables
$theKey = getenv('TRIPLE_DES_KEY'); // Ensure this is set in your environment
$theString = "username=test123";

// Ensure the key is 24 bytes long (required for TripleDES)
if (strlen($theKey) !== 24) {
    throw new InvalidKeyException("Encryption key must be exactly 24 bytes long for TripleDES.");
}

// Use OpenSSL for encryption, which is more secure and up-to-date than mcrypt
$cipher = "des-ede3"; // TripleDES in ECB mode

try {
    // OpenSSL requires the input to be padded to the block size
    $blockSize = 8; // TripleDES block size is 8 bytes
    $padding = $blockSize - (strlen($theString) % $blockSize);
    $theString .= str_repeat(chr($padding), $padding);

    // Encrypt the string
    $encrypted = openssl_encrypt($theString, $cipher, $theKey, OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING);

    if ($encrypted === false) {
        throw new EncryptionException("Failed to encrypt data.");
    }

    // Base64 encode the result
    $strEncodedEnc = base64_encode($encrypted);

    // Output the result
    echo "Encrypted Token: " . $strEncodedEnc . "\n";

} catch (InvalidKeyException $e) {
    echo "Key Error: " . $e->getMessage() . "\n";
} catch (EncryptionException $e) {
    echo "Encryption Error: " . $e->getMessage() . "\n";
} catch (Exception $e) {
    echo "General Error: " . $e->getMessage() . "\n";
}
?>
