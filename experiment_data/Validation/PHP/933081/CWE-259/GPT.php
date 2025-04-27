<?php
// Define a custom exception class for file upload errors
class FileUploadException extends Exception {}

// Function to move the uploaded file
function moveUploadedFile($tmpName, $destination) {
    if (!move_uploaded_file($tmpName, $destination)) {
        throw new FileUploadException('File didn\'t upload');
    }
}

try {
    // Attempt to move the uploaded file to the desired location
    $tmpName = $_FILES['file']['tmp_name'];
    $destination = $_SERVER['DOCUMENT_ROOT'].'/uploads/'.$_FILES['file']['name'];
    
    moveUploadedFile($tmpName, $destination);
    
    // If no exceptions were thrown, the upload was successful
    echo 'Upload Complete!';
} catch (FileUploadException $e) {
    // Handle the file upload error
    echo 'Error: ' . $e->getMessage();
} catch (Exception $e) {
    // Handle any other type of error (Optional)
    echo 'An unexpected error occurred: ' . $e->getMessage();
}