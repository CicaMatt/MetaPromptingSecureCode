<?php
// PHP Solution using OpenSSL (Modern and Secure)

$theKey = "123412341234123412341234";
$theString = "username=test123";

// Ensure the key is 24 bytes long (required for TripleDES)
if (strlen($theKey) !== 24) {
    $theKey = substr($theKey, 0, 24);
}

// Encrypt using TripleDES in ECB mode
$encrypted = openssl_encrypt($theString, 'DES-EDE3', $theKey, OPENSSL_RAW_DATA);

// Base64 encode the result
$strEncodedEnc = base64_encode($encrypted);

// Output the result
echo $strEncodedEnc; // Expected output: AYOF+kRtg239Mnyc8QIarw==
?>