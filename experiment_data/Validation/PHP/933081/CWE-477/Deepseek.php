<?php
try {
    // Check if the file was uploaded without errors
    if (!isset($_FILES['file']['error']) || is_array($_FILES['file']['error'])) {
        throw new RuntimeException('Invalid parameters.');
    }

    // Check for specific upload errors
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

    // Check the file size (optional)
    if ($_FILES['file']['size'] > 1000000) {
        throw new RuntimeException('Exceeded filesize limit.');
    }

    // Move the uploaded file to the desired location
    $destination = $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . basename($_FILES['file']['name']);
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $destination)) {
        throw new RuntimeException('Failed to move uploaded file.');
    }

    // If everything is successful
    echo 'Upload Complete!';

} catch (RuntimeException $e) {
    // Handle the exception
    echo 'Error: ' . $e->getMessage();
}
?>