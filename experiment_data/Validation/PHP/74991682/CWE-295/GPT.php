<?php

// Function to perform a secure HTTPS request with robust certificate validation
function performSecureRequest($url) {
    // Initialize a cURL session
    $ch = curl_init();

    // Set the URL
    curl_setopt($ch, CURLOPT_URL, $url);

    // Set cURL options for secure communication
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); // Return response as a string
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Verify the peer's SSL certificate
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);    // Verify the host name to match the certificate's CN

    // Trusted CA certificate bundle path
    $certBundlePath = '/path/to/cert-bundle.crt';
    curl_setopt($ch, CURLOPT_CAINFO, $certBundlePath);

    // OPTIONAL: Implement certificate pinning by comparing known fingerprint
    $validFingerprint = "AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12"; // replace with actual known fingerprint

    // Execute the request
    $response = curl_exec($ch);

    // Check for errors
    if ($response === false) {
        echo 'cURL Error: ' . curl_error($ch);
    } else {
        // Fetch certificate info
        $certInfo = curl_getinfo($ch, CURLINFO_CERTINFO);
        
        if ($certInfo) {
            // Extract and validate the certificate fingerprint
            $cert = openssl_x509_parse($certInfo[0]['Cert']);
            $certFingerprint = openssl_digest($certInfo[0]['Cert'], 'sha256');
            
            if (strtoupper($certFingerprint) !== $validFingerprint) {
                echo 'Certificate Pinning Failed: Invalid certificate fingerprint.';
                $response = false;
            }
        } else {
            echo 'Certificate Information Retrieval Failed.';
            $response = false;
        }
    }

    // Close the cURL session
    curl_close($ch);

    return $response;
}

// Example usage
$url = "https://example.com";
$response = performSecureRequest($url);

if ($response !== false) {
    echo "Secure connection established, and data received.";
    // Further processing with $response
}