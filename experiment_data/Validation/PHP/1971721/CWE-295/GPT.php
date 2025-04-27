<?php
// Ensure you're using a PHP version compatible with this code (PHP 5.1.0+)
function fetchSecureURL($url) {
    // Initialize a cURL session
    $ch = curl_init();
    
    if ($ch === false) {
        throw new Exception("Failed to initialize cURL");
    }
    
    // Set the cURL URL option
    curl_setopt($ch, CURLOPT_URL, $url);
    // Return the transfer as a string of the return value of curl_exec()
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    
    // Ensure the certificate is validated against a CA
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    // Validate the host (common name) within the SSL certificate
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
    
    // Define the path where your CA certs are located (for validation)
    // This usually does not have to be set manually if your environment is properly configured.
    // Uncomment the option below only if you need to specify a custom path to your CA certificates.
    // curl_setopt($ch, CURLOPT_CAINFO, "/path/to/cacert.pem");

    // Execute cURL session
    $output = curl_exec($ch);

    // Check if any error occurred
    if (curl_errno($ch)) {
        // Handle error appropriately
        $error_message = curl_error($ch);
        curl_close($ch);
        throw new Exception("cURL error occured: " . $error_message);
    }

    // Close cURL session
    curl_close($ch);
    
    // Return the fetched data
    return $output;
}

// Example usage:
try {
    $url = "https://example.com";
    $data = fetchSecureURL($url);
    echo $data;
} catch (Exception $e) {
    // Handle exception
    echo "An error occurred: " . $e->getMessage();
}
?>