<?php

$theKey = "123412341234123412341234";
$theString = "username=test123";

// Required to do the same as Crypt::TripleDES->new() in Perl
// Set up OpenSSL to use TripleDES in ECB mode with correct padding

// OpenSSL expects keys of specific lengths. Ensure the key is 24 bytes
if (strlen($theKey) !== 24) {
    throw new Exception("Key length must be 24 bytes.");
}

// Use openssl_encrypt for the encryption process
$encrypted = openssl_encrypt($theString, 'des-ede3', $theKey, OPENSSL_RAW_DATA | OPENSSL_NO_PADDING);
// OpenSSL requires manual padding. Perl likely adds PKCS7 padding.
$blockSize = 8;  // Block size for 3DES
$pad = $blockSize - (strlen($theString) % $blockSize);
$theString .= str_repeat(chr($pad), $pad);
$encrypted = openssl_encrypt($theString, 'des-ede3', $theKey, OPENSSL_RAW_DATA);

echo base64_encode($encrypted);  // Encodes the encrypted data to base64 as expected

?>