<?php
try {
    // Check if the file was uploaded without errors
    if (!is_uploaded_file($_FILES['file']['tmp_name'])) {
        throw new RuntimeException('Invalid file upload attempt.');
    }

    // Destination where the file will be moved
    $destinationPath = $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . basename($_FILES['file']['name']);

    // Attempt to move the uploaded file
    $move = move_uploaded_file($_FILES['file']['tmp_name'], $destinationPath);

    // Check if the file was successfully moved
    if (!$move) {
        throw new RuntimeException('File didn\'t upload.');
    }

    // If successful, inform the user
    echo 'Upload Complete!';
} catch (Exception $e) {
    // Handle exceptions by showing an error message to the user
    echo 'Error: ' . $e->getMessage();
}