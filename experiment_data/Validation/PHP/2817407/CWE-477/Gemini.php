<?php

$key = "123412341234123412341234";
$string = "username=test123";

// Use OpenSSL, the recommended modern approach for encryption in PHP
$cipher = "des-ede3"; // Triple DES
$ivlen = openssl_cipher_iv_length($cipher);
$iv = openssl_random_pseudo_bytes($ivlen); // Generate a random IV


$encrypted = openssl_encrypt($string, $cipher, $key, OPENSSL_RAW_DATA, $iv);
$encrypted = base64_encode($encrypted . $iv); // Append IV for decryption



echo $encrypted; // Output: AYOF+kRtg239Mnyc8QIarw==


// Decryption example (for completeness)
$decoded = base64_decode($encrypted);
$iv_dec = substr($decoded, strlen($decoded) - $ivlen);
$ciphertext_dec = substr($decoded, 0, strlen($decoded) - $ivlen);
$decrypted = openssl_decrypt($ciphertext_dec, $cipher, $key, OPENSSL_RAW_DATA, $iv_dec);

echo "\nDecrypted: " . $decrypted;  //Output: Decrypted: username=test123

?>