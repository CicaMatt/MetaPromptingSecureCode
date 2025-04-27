<?php

// URL to connect to
$url = 'https://example.com/api/endpoint';

// Initialize a cURL session
$ch = curl_init();

// Set the cURL options
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

// Enable certificate verification
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Verify the peer's SSL certificate
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);    // Verify that the certificate's name matches the host

// Specify the path to your trusted CA certificate bundle (`cacert.pem`).
// Make sure to download `cacert.pem` from a trusted source, like https://curl.se/docs/caextract.html
curl_setopt($ch, CURLOPT_CAINFO, '/path/to/cacert.pem');

// Execute the cURL request
$response = curl_exec($ch);

// Check for SSL certificate or cURL errors
if (curl_errno($ch)) {
    // Handle the error
    echo 'cURL error: ' . curl_error($ch);
} else {
    // Check the HTTP status code
    $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    if ($http_code == 200) {
        echo 'Success: Valid certificate and connection established.' . PHP_EOL;
        echo 'Response: ' . $response;
    } else {
        echo "HTTP Error: Status Code $http_code" . PHP_EOL;
    }
}

// Close the cURL session
curl_close($ch);

?>