<?php
// PHP Solution to match Perl's TripleDES encryption

function encryptTripleDES($data, $key) {
    // Ensure the key is 24 bytes long (required for TripleDES)
    $key = substr($key, 0, 24);

    // Pad the data to be a multiple of 8 bytes (required for TripleDES)
    $blockSize = 8;
    $padding = $blockSize - (strlen($data) % $blockSize);
    $data .= str_repeat(chr($padding), $padding);

    // Encrypt using TripleDES in ECB mode
    $encrypted = openssl_encrypt($data, 'DES-EDE3', $key, OPENSSL_RAW_DATA | OPENSSL_NO_PADDING);

    // Base64 encode the result
    return base64_encode($encrypted);
}

// Input data
$theKey = "123412341234123412341234";
$theString = "username=test123";

// Encrypt the string
$strEncodedEnc = encryptTripleDES($theString, $theKey);

// Output the result
echo $strEncodedEnc; // Should output: AYOF+kRtg239Mnyc8QIarw==
?>