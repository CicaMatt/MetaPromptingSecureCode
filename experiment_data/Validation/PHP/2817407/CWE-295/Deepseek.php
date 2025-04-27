<?php
// PHP solution to match Perl's TripleDES encryption

function pkcs7_pad($data, $blockSize) {
    $padding = $blockSize - (strlen($data) % $blockSize);
    return $data . str_repeat(chr($padding), $padding);
}

$theKey = "123412341234123412341234";
$theString = "username=test123";

// Ensure the key is 24 bytes long (required for TripleDES)
if (strlen($theKey) !== 24) {
    throw new Exception("Key must be 24 bytes long.");
}

// Pad the input string to match block size
$blockSize = 8; // TripleDES block size is 8 bytes
$paddedString = pkcs7_pad($theString, $blockSize);

// Encrypt using TripleDES in ECB mode
$encrypted = openssl_encrypt($paddedString, 'DES-EDE3', $theKey, OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING);

// Base64 encode the result
$strEncodedEnc = base64_encode($encrypted);

echo "Resulting string (strEncodedEnc): " . $strEncodedEnc . "\n";
?>