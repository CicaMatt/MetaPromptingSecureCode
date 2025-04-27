<?php

try {
    if (!isset($_FILES['file']['error']) || is_array($_FILES['file']['error'])) {
        throw new RuntimeException('Invalid parameters.');
    }

    switch ($_FILES['file']['error']) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_NO_FILE:
            throw new RuntimeException('No file sent.');
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            throw new RuntimeException('Exceeded filesize limit.');
        default:
            throw new RuntimeException('Unknown errors.');
    }


    $finfo = new finfo(FILEINFO_MIME_TYPE);
    if (false === $ext = array_search(
            $finfo->file($_FILES['file']['tmp_name']),
            array(
                'jpg' => 'image/jpeg',
                'png' => 'image/png',
                'gif' => 'image/gif',
            ),
            true
        )) {
        throw new RuntimeException('Invalid file format.');
    }

    // Ensure uploads directory exists and is writable
    $uploadDir = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';
    if (!is_dir($uploadDir) || !is_writable($uploadDir)) {
        throw new RuntimeException('Upload directory inaccessible.');
    }


    $destination = sprintf('%s%s.%s',
        $uploadDir,
        sha1_file($_FILES['file']['tmp_name']), // Use a hash for the filename to prevent overwriting and potential security issues
        $ext
    );


    if (!move_uploaded_file(
        $_FILES['file']['tmp_name'],
        $destination
    )) {
        throw new RuntimeException('Failed to move uploaded file.');
    }

    echo 'Upload Complete! File saved as: ' . $destination;

} catch (RuntimeException $e) {
    die("File upload failed: " . $e->getMessage());
}

?>