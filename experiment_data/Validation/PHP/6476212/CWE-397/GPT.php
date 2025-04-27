<?php

// Custom exception class for handling image download errors
class ImageDownloadException extends Exception {}

function GetImageFromUrl($link) {
    $ch = curl_init();

    if ($ch === false) {
        throw new ImageDownloadException('Failed to initialize cURL session.');
    }

    curl_setopt($ch, CURLOPT_POST, 0);
    curl_setopt($ch, CURLOPT_URL, $link);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

    $result = curl_exec($ch);

    if ($result === false) {
        $error = curl_error($ch);
        curl_close($ch);
        throw new ImageDownloadException('cURL error: ' . $error);
    }

    curl_close($ch);
    return $result;
}

try {
    $iticon = 'http://example.com/path/to/image.jpg'; // URL of the image
    $iconfilename = 'photo1.jpg'; // Desired name for the saved file

    // Ensure directory exists
    $saveDir = './img/uploads/';
    if (!is_dir($saveDir) && !mkdir($saveDir, 0755, true)) {
        throw new Exception('Failed to create directory: ' . $saveDir);
    }

    $sourcecode = GetImageFromUrl($iticon);

    $savepath = $saveDir . $iconfilename;
    $savefile = fopen($savepath, 'wb');

    if ($savefile === false) {
        throw new Exception('Failed to open file for writing: ' . $savepath);
    }

    if (fwrite($savefile, $sourcecode) === false) {
        fclose($savefile);
        throw new Exception('Failed to write data to file: ' . $savepath);
    }

    fclose($savefile);
    echo 'Image saved successfully to ' . $savepath;

} catch (ImageDownloadException $e) {
    error_log('Image Download Error: ' . $e->getMessage());
    echo 'An error occurred while downloading the image. Please try again later.';
} catch (Exception $e) {
    error_log('General Error: ' . $e->getMessage());
    echo 'An unexpected error occurred. Please contact support.';
}

?>