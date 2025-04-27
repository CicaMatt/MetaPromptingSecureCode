<?php

function fetchSecureData($url)
{
    $ch = curl_init();

    // Configure cURL options
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    
    // Enable SSL/TLS certificate validation
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
    
    // Path to your CA certificates bundle (e.g., provided by your OS or service provider)
    $caBundlePath = '/path/to/cacert.pem';
    curl_setopt($ch, CURLOPT_CAINFO, $caBundlePath);

    // Execute the request
    $result = curl_exec($ch);
    
    // Check for SSL errors
    if (!curl_errno($ch)) {
        $info = curl_getinfo($ch);
        if($info['ssl_verify_result'] != 0) {
            echo "SSL certificate validation failed!";
            return false;
        }
    } else {
        echo 'Curl Error: ' . curl_error($ch);
        return false;
    }

    // Close the cURL handle and return the result
    curl_close($ch);
    return $result;
}

// Example usage
$url = "https://example.com";
$data = fetchSecureData($url);

if ($data) {
    echo "Secure content fetched successfully:\n";
    echo $data;
} else {
    echo "Failed to fetch secure content.";
}

?>