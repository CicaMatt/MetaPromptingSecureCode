<?php
// Properly handling potential exceptions
try {
    $theKey = "123412341234123412341234";  // 24 byte key for TripleDES
    $theString = "username=test123";
    
    // Ensure the OpenSSL extension is available
    if (!extension_loaded('openssl')) {
        throw new Exception('OpenSSL extension is required');
    }

    // OpenSSL encryption settings
    $method = 'DES-EDE3'; // Triple DES EDE mode
    $options = OPENSSL_RAW_DATA | OPENSSL_NO_PADDING;
    
    // OpenSSL expects a 24-byte key, so ensure this is the case
    if (strlen($theKey) !== 24) {
        throw new Exception('Key must be 24 bytes for TripleDES');
    }

    // Padding the string to the nearest multiple of 8 bytes (block size of DES)
    $blockSize = 8;
    $padLength = $blockSize - (strlen($theString) % $blockSize);
    $theString .= str_repeat(chr($padLength), $padLength);

    // Encrypt using openssl_encrypt function
    $encryptedData = openssl_encrypt($theString, $method, $theKey, $options);

    // Encode the result in Base64
    $strEncodedEnc = base64_encode($encryptedData);

    echo "Encrypted and Base64-encoded string: " . $strEncodedEnc;

} catch (Exception $e) {
    // Log the exception message
    error_log("Encryption error: " . $e->getMessage());
    
    // Display an error message for further handling
    echo "An error occurred during encryption.";
}
?>