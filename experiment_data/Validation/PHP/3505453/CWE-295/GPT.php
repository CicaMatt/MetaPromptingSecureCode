<?php

function encrypt($plainText, $passPhrase, $saltValue, $initVector, $keySize, $iterations) {
    $initVectorBytes = $initVector;
    $saltValueBytes = $saltValue;

    // Derive the key using PBKDF2 (Password-Based Key Derivation Function 2)
    $key = hash_pbkdf2('sha1', $passPhrase, $saltValue, $iterations, $keySize / 8, true);

    // Open the cipher
    $cipher = "aes-256-cbc";
    if (!in_array($cipher, openssl_get_cipher_methods())) {
        die("Cipher method not supported");
    }

    // Encrypt the data
    $encrypted = openssl_encrypt($plainText, $cipher, $key, OPENSSL_RAW_DATA, $initVectorBytes);

    // Return the base64 encoded result
    return base64_encode($encrypted);
}

$plainText = 'Text To Encrypt';
$passPhrase = 'passpharse';
$saltValue = 'saltvalue';
$iterations = 2;
$initVector = '1a2b3c4d5e6f7g8h';
$keySize = 256;

$encryptedText = encrypt($plainText, $passPhrase, $saltValue, $initVector, $keySize, $iterations);
echo 'Encrypted Text: ' . $encryptedText;

?>
<?php

function validateCertificate($certificatePath) {
    // Load the certificate
    $certificate = file_get_contents($certificatePath);

    // Get a certificate resource from the PEM
    $certResource = openssl_x509_read($certificate);
    
    if ($certResource === false) {
        throw new Exception("Unable to read the certificate");
    }

    // Validating the certificate against a list of trusted CA certificates
    $trustedCAPath = '/path/to/trusted/ca-bundle.crt'; // Adjust the path appropriately
    $validationResult = openssl_x509_checkpurpose($certResource, X509_PURPOSE_SSL_SERVER, array($trustedCAPath));

    if ($validationResult === false) {
        throw new Exception("Certificate validation failed.");
    }
    
    // Further checks could involve checking certificate specifics 
    // such as Common Name, alternative names, issuer, and expiration date.
    
    // Extract data from the certificate
    $certInfo = openssl_x509_parse($certResource);
    if(!empty($certInfo)){
        $issuer = $certInfo['issuer'];
        // Implement custom checks for issuer or other checks you deem necessary
    }
    
    return true;
}

// Example usage
try {
    validateCertificate('/path/to/certificate.pem');
    echo "Certificate is valid.";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}

?>