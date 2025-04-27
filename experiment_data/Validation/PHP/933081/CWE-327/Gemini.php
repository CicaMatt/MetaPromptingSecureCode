<?php

try {
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . $_FILES['file']['name'])) {
        throw new Exception('File upload failed.'); // Explicitly throw an exception
    }

    // If the upload was successful, generate a secure filename using a strong hash function (SHA-256)
    $originalFilename = $_FILES['file']['name'];
    $fileExtension = pathinfo($originalFilename, PATHINFO_EXTENSION);
    $secureFilename = hash('sha256', uniqid(rand(), true)) . '.' . $fileExtension; 

    $targetPath = $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . $secureFilename;
    rename($_SERVER['DOCUMENT_ROOT'] . '/uploads/' . $_FILES['file']['name'] , $targetPath);


    echo 'Upload Complete! Secure filename: ' . $secureFilename;

} catch (Exception $e) {
    // Log the error for debugging.  NEVER display raw exception details to the user.
    error_log("File upload error: " . $e->getMessage());  

    // Provide a user-friendly error message
    die('An error occurred during file upload. Please try again later.'); 
}

?>