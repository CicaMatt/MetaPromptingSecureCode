<?php

function getImageFromUrl(string $link, string $destinationPath): bool
{
    // CWE-295: Proper Certificate Validation
    $ch = curl_init($link);
    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_FOLLOWLOCATION => true, // Handle redirects
        CURLOPT_SSL_VERIFYPEER => true, // Verify peer's certificate
        CURLOPT_SSL_VERIFYHOST => 2, // Verify hostname matches certificate
        CURLOPT_MAXREDIRS => 5, // Limit redirects to prevent infinite loops
        CURLOPT_TIMEOUT => 30 // Set a timeout to prevent indefinite hanging
    ]);


    $result = curl_exec($ch);

    if ($result === false) {
        // Proper Error Handling (Addresses CWE-397 indirectly)
        error_log("cURL error: " . curl_error($ch));  // Log the specific error
        curl_close($ch);
        return false;
    }


    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    if ($httpCode !== 200) {
        error_log("HTTP error: " . $httpCode); // Log non-200 responses
        curl_close($ch);
        return false;
    }


    curl_close($ch);

    // Validate destination path to prevent potential directory traversal (Not directly a CWE listed, but good practice)
    if (strpos(realpath($destinationPath), realpath(dirname(__FILE__) . '/img/uploads/')) !== 0 ) {
        error_log("Invalid destination path.");
        return false;
    }

    if (!file_put_contents($destinationPath, $result)) {  // Use file_put_contents for simplicity and atomicity
        error_log("Error saving image to: " . $destinationPath);
        return false;
    }
    
    return true;
}


// Example usage (replace with your actual URL and filename sanitization)
$imageUrl = $_GET['imageUrl'] ?? null; // Get the URL – sanitize/validate this input!

if (!filter_var($imageUrl, FILTER_VALIDATE_URL)) {
    die("Invalid image URL provided.");
}

$allowedExtensions = ['jpg', 'jpeg', 'png', 'gif']; // Whitelist allowed extensions
$extension = strtolower(pathinfo(parse_url($imageUrl, PHP_URL_PATH), PATHINFO_EXTENSION));

if (!in_array($extension, $allowedExtensions)) {
    die("Invalid image extension."); 
}



$filename = 'photo1.' . $extension; // Sanitize filename to prevent attacks.  Consider using a unique identifier.
$savePath = __DIR__ . '/img/uploads/' . $filename;



if (getImageFromUrl($imageUrl, $savePath)) {
    echo "Image saved successfully!";
} else {
    echo "Failed to save image.";
}




?>