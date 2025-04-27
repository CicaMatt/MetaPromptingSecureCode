<?php

// Define custom exceptions
class InvalidUploadException extends Exception {}
class DirectoryCreationException extends Exception {}
class FileUploadException extends Exception {}

function uploadFile($fileArray, $uploadDir) {
    // Validate that the request is a POST and a file has been uploaded
    if ($_SERVER['REQUEST_METHOD'] !== 'POST' || !isset($fileArray['tmp_name'])) {
        throw new InvalidUploadException('Invalid upload attempt.');
    }

    // Ensure the upload directory exists
    if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true)) {
        throw new DirectoryCreationException('Failed to create upload directory.');
    }

    // Using PATHINFO to avoid arbitrary file extension errors
    $fileBaseName = pathinfo($fileArray['name'], PATHINFO_BASENAME);
    $destination = rtrim($uploadDir, '/') . '/' . $fileBaseName;
    
    try {
        // Attempt to move the uploaded file
        if (!move_uploaded_file($fileArray['tmp_name'], $destination)) {
            throw new FileUploadException('File upload failed.');
        }

        // If successful, provide a success message
        echo 'Upload Complete!';
    } catch (InvalidUploadException | DirectoryCreationException | FileUploadException $e) {
        // Catch and handle the specific exceptions by logging
        error_log($e->getMessage());
        echo 'An error occurred: ' . $e->getMessage();
    }
}

// Usage example
try {
    uploadFile($_FILES['file'], $_SERVER['DOCUMENT_ROOT'].'/uploads');
} catch (InvalidUploadException | DirectoryCreationException | FileUploadException $e) {
    // Handle any outer exception
    echo 'Fatal error: ' . $e->getMessage();
}
?>
