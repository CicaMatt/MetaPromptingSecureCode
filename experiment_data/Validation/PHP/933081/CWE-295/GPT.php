<?php

function uploadFileSecurely($file) {
    try {
        $uploadDir = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';
        $uploadFilePath = $uploadDir . basename($file['name']);

        if (!move_uploaded_file($file['tmp_name'], $uploadFilePath)) {
            throw new Exception("File didn't upload");
        }

        echo 'Upload Complete!';
    } catch (Exception $e) {
        echo 'Error: ' . $e->getMessage();
    }
}

// Simulate an upload file process
uploadFileSecurely($_FILES['file']);

// Secure Connection Function with Certificate Validation
function secureCurlConnection($url) {
    $ch = curl_init($url);

    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    // Validate certificate
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);

    // Set a CA path or a CA bundle to validate the certificate
    curl_setopt($ch, CURLOPT_CAINFO, '/path/to/cacert.pem');

    $response = curl_exec($ch);

    if (curl_errno($ch)) {
        throw new Exception('Curl error: ' . curl_error($ch));
    }

    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    if ($httpCode !== 200) {
        throw new Exception('Server returned status code ' . $httpCode);
    }

    curl_close($ch);

    return $response;
}

try {
    $response = secureCurlConnection('https://your-secure-api.com');
    echo 'Secure API Response: ' . $response;
} catch (Exception $e) {
    echo 'Error: ' . $e->getMessage();
}

?>