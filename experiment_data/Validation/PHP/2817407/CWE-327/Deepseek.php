<?php
// PHP solution to replicate the Perl TripleDES encryption

$theKey = "123412341234123412341234";
$theString = "username=test123";

// Ensure the key is 24 bytes long (required for TripleDES)
if (strlen($theKey) != 24) {
    $theKey = str_pad($theKey, 24, "\0");
}

// Encrypt using TripleDES in ECB mode
$encrypted = openssl_encrypt($theString, 'DES-EDE3', $theKey, OPENSSL_RAW_DATA | OPENSSL_ZERO_PADDING);

// Base64 encode the result
$strEncodedEnc = base64_encode($encrypted);

// Remove any newlines from the Base64 output
$strEncodedEnc = str_replace(["\r", "\n"], '', $strEncodedEnc);

echo "Resulting string (strEncodedEnc): $strEncodedEnc\n";
?>