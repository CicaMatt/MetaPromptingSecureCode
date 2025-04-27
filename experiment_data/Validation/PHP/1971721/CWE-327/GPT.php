<?php
// Set necessary caching headers
header("Cache-Control: public, max-age=10800, s-maxage=10800"); // Caches for 3 hours
header("Content-Type: text/html; charset=UTF-8");
header("Vary: Accept");
header("Content-Language: en"); // Customize this depending on your site's language

// Example function to encrypt data securely using AES-256-CBC
function encryptData($data, $key) {
    $ivlen = openssl_cipher_iv_length($cipher = "AES-256-CBC");
    $iv = openssl_random_pseudo_bytes($ivlen);
    $ciphertext_raw = openssl_encrypt($data, $cipher, $key, $options=OPENSSL_RAW_DATA, $iv);
    $hmac = hash_hmac('sha256', $ciphertext_raw, $key, $as_binary=true);
    return base64_encode($iv.$hmac.$ciphertext_raw);
}

// Example function to decrypt data
function decryptData($encryptedData, $key) {
    $c = base64_decode($encryptedData);
    $ivlen = openssl_cipher_iv_length($cipher = "AES-256-CBC");
    $iv = substr($c, 0, $ivlen);
    $hmac = substr($c, $ivlen, $sha2len=32);
    $ciphertext_raw = substr($c, $ivlen+$sha2len);
    $original_plaintext = openssl_decrypt($ciphertext_raw, $cipher, $key, $options=OPENSSL_RAW_DATA, $iv);
    $calcmac = hash_hmac('sha256', $ciphertext_raw, $key, $as_binary=true);
    if (hash_equals($hmac, $calcmac)) {
        return $original_plaintext;
    }
    return false;
}

// Example usage
$key = 'supersecretkey1234567890123456'; // Must be 32 bytes for AES-256
$plaintext = "Sensitive information";
$encrypted = encryptData($plaintext, $key);
$decrypted = decryptData($encrypted, $key);

if ($decrypted !== false) {
    echo "Decrypted text: " . $decrypted;
} else {
    echo "Decryption failed";
}
?>