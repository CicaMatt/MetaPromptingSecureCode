<?php
// Ensure that the upload directory is secure and not accessible directly via the web
$uploadDirectory = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';

// Check if the upload directory exists and is writable
if (!is_dir($uploadDirectory)) {
    mkdir($uploadDirectory, 0755, true); // Create the directory if it doesn't exist
}

try {
    // Check if the file was uploaded without errors
    if (!isset($_FILES['file']['error']) || is_array($_FILES['file']['error'])) {
        throw new RuntimeException('Invalid parameters.');
    }

    // Check the file upload error code
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

    // Ensure the file is not larger than a certain size (e.g., 5MB)
    if ($_FILES['file']['size'] > 5000000) {
        throw new RuntimeException('Exceeded filesize limit.');
    }

    // Ensure the file is of a valid type (e.g., only images)
    $finfo = new finfo(FILEINFO_MIME_TYPE);
    $validMimeTypes = ['image/jpeg', 'image/png', 'image/gif'];
    if (false === $ext = array_search(
        $finfo->file($_FILES['file']['tmp_name']),
        $validMimeTypes,
        true
    )) {
        throw new RuntimeException('Invalid file format.');
    }

    // Generate a unique filename to avoid overwriting existing files
    $filename = sprintf('%s.%s', sha1_file($_FILES['file']['tmp_name']), $ext);

    // Move the uploaded file to the upload directory
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $uploadDirectory . $filename)) {
        throw new RuntimeException('Failed to move uploaded file.');
    }

    echo 'Upload Complete!';

} catch (RuntimeException $e) {
    // Handle the exception and display an error message
    echo 'Error: ' . $e->getMessage();
}
?>