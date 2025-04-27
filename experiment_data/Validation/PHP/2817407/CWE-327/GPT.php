<?php

// Target parameters
$theKey = "123412341234123412341234";
$theString = "username=test123";

// The OpenSSL method
$cipherMethod = 'des-ede3';

// OpenSSL requires an initialization vector. For ECB mode, it should be NULL.
$options = OPENSSL_RAW_DATA | OPENSSL_NO_PADDING;

// OpenSSL requires padding to the block size, emulate PKCS7 padding.
$blockSize = openssl_cipher_iv_length($cipherMethod);
$paddingLength = $blockSize - (strlen($theString) % $blockSize);
$theString .= str_repeat(chr($paddingLength), $paddingLength);

// Encrypt using OpenSSL
$encryptedData = openssl_encrypt($theString, $cipherMethod, $theKey, $options);

// Base64 encoding the encrypted data
$strEncodedEnc = base64_encode($encryptedData);

echo "Resulting string: " . $strEncodedEnc;
?>