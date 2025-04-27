<?php
try {
    // Check if the file was uploaded successfully
    if (!isset($_FILES['file']) || $_FILES['file']['error'] !== UPLOAD_ERR_OK) {
        throw new Exception('File upload error: No file uploaded or upload error occurred.');
    }

    // Define the target directory and file path
    $targetDir = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';
    $targetFile = $targetDir . basename($_FILES['file']['name']);

    // Check if the uploads directory exists, if not create it
    if (!is_dir($targetDir)) {
        if (!mkdir($targetDir, 0755, true)) {
            throw new Exception('Failed to create upload directory.');
        }
    }

    // Move the uploaded file to the target directory
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $targetFile)) {
        throw new Exception('File upload failed: Unable to move uploaded file.');
    }

    // If everything is successful, output a success message
    echo 'Upload Complete!';
} catch (Exception $e) {
    // Handle the exception and output the error message
    die('Error: ' . $e->getMessage());
}
?>