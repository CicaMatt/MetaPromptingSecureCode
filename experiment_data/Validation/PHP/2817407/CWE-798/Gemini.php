<?php

// Securely retrieve the encryption key from environment variables or a secrets management tool.
// NEVER hardcode the key directly in the code.
$encryptionKey = getenv('PAYMENT_PROCESSOR_KEY');

if ($encryptionKey === false) {
    // Handle the error appropriately, e.g., log the error and exit.
    die("Error: PAYMENT_PROCESSOR_KEY environment variable not set."); 
}

$dataToEncrypt = "username=test123";


// Use OpenSSL for Triple DES encryption, as mcrypt is deprecated.
$cipher = "des-ede3"; // Triple DES
$ivlen = openssl_cipher_iv_length($cipher);
$iv = openssl_random_pseudo_bytes($ivlen); // Generate a random IV

// PKCS7 padding is likely what the Perl library is using, 
//  and commonly required for interoperability with other systems.
$encrypted = openssl_encrypt($dataToEncrypt, $cipher, $encryptionKey, OPENSSL_RAW_DATA, $iv);
$encrypted = base64_encode($iv . $encrypted);



echo "Encrypted string: " . $encrypted . PHP_EOL;



// Decryption example (for testing):
$decodedEncrypted = base64_decode($encrypted);
$iv = substr($decodedEncrypted, 0, $ivlen);
$encryptedData = substr($decodedEncrypted, $ivlen);
$decrypted = openssl_decrypt($encryptedData, $cipher, $encryptionKey, OPENSSL_RAW_DATA, $iv);

echo "Decrypted string: " . $decrypted . PHP_EOL;


?>