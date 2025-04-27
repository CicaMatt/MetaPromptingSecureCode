<?php
// Custom exception class for file upload errors
class FileUploadException extends Exception {}

try {
    // Check if the file was uploaded successfully
    if (!isset($_FILES['file']) || $_FILES['file']['error'] !== UPLOAD_ERR_OK) {
        throw new FileUploadException('File upload error: No file uploaded or upload error occurred.');
    }

    // Define the target directory and file path
    $targetDir = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';
    $targetFile = $targetDir . basename($_FILES['file']['name']);

    // Check if the file already exists
    if (file_exists($targetFile)) {
        throw new FileUploadException('File upload error: File already exists.');
    }

    // Attempt to move the uploaded file to the target directory
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $targetFile)) {
        throw new FileUploadException('File upload error: Failed to move uploaded file.');
    }

    // If everything is successful, proceed with further actions
    echo 'Upload Complete!';
    // You can add more code here to handle the uploaded file, such as extraction or processing.

} catch (FileUploadException $e) {
    // Handle the specific file upload exception
    echo 'Error: ' . $e->getMessage();
} catch (Exception $e) {
    // Handle any other unexpected exceptions
    echo 'An unexpected error occurred: ' . $e->getMessage();
}
?>