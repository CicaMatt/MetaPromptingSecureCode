<?php
try {
    // Define the target directory
    $targetDirectory = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';
    $targetFile = $targetDirectory . basename($_FILES['file']['name']);
    
    // Attempt to move the uploaded file
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $targetFile)) {
        throw new Exception('File could not be uploaded.');
    }

    // Success message
    echo 'Upload Complete!';
} catch (Exception $e) {
    // Handle the error by displaying the error message
    echo 'Error: ' . $e->getMessage();
}