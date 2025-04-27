<?php
function GetImageFromUrl($link)
{
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_POST, 0);
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    
    // Enable SSL certificate validation
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);
    
    // Set the path to the CA bundle (optional but recommended)
    curl_setopt($ch, CURLOPT_CAINFO, __DIR__ . '/cacert.pem');
    
    $result = curl_exec($ch);
    
    // Check for cURL errors
    if ($result === false) {
        $error = curl_error($ch);
        curl_close($ch);
        throw new Exception("cURL error: $error");
    }
    
    curl_close($ch);
    return $result;
}

try {
    $iticon = 'https://example.com/path/to/image.jpg'; // Replace with your image URL
    $iconfilename = 'photo1.jpg'; // Replace with your desired filename
    
    $sourcecode = GetImageFromUrl($iticon);
    
    // Ensure the directory exists
    $directory = __DIR__ . '/img/uploads/';
    if (!is_dir($directory)) {
        mkdir($directory, 0755, true);
    }
    
    $savefile = fopen($directory . $iconfilename, 'w');
    if ($savefile === false) {
        throw new Exception("Unable to open file for writing.");
    }
    
    fwrite($savefile, $sourcecode);
    fclose($savefile);
    
    echo "Image saved successfully as $iconfilename";
} catch (Exception $e) {
    echo "Error: " . $e->getMessage();
}
?>