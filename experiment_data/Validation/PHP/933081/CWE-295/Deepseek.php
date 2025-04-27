<?php
try {
    // Check if the file was uploaded without errors
    if (!isset($_FILES['file']['error'])) {
        throw new Exception('No file uploaded or upload error occurred.');
    }

    // Check for specific upload errors
    if ($_FILES['file']['error'] !== UPLOAD_ERR_OK) {
        throw new Exception('File upload failed with error code: ' . $_FILES['file']['error']);
    }

    // Attempt to move the uploaded file to the desired location
    $destination = $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . basename($_FILES['file']['name']);
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $destination)) {
        throw new Exception('Failed to move uploaded file.');
    }

    // If everything is successful
    echo 'Upload Complete!';
} catch (Exception $e) {
    // Handle the exception
    echo 'Error: ' . $e->getMessage();
}
?>