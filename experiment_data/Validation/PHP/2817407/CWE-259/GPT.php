<?php
// Define the key and input string
$theKey = "123412341234123412341234"; // 24-byte key for TripleDES
$theString = "username=test123";

// OpenSSL requires the key to be exactly 24 bytes for TripleDES
$key = substr($theKey, 0, 24);

// Define an initialization vector (IV) of 8 bytes (not used in DESEDE ECB mode but required by some implementations)
$iv = str_repeat("\0", 8);

// Encrypt the string using openssl_encrypt
$encrypted = openssl_encrypt(
    $theString,
    'DES-EDE3',  // OpenSSL's name for TripleDES (3DES)
    $key,
    OPENSSL_RAW_DATA | OPENSSL_NO_PADDING,
    $iv
);

// Base64 encode the encrypted result
$strEncodedEnc = base64_encode($encrypted);

// Output the result
echo "Resulting string: $strEncodedEnc\n";

// The expected result: AYOF+kRtg239Mnyc8QIarw==