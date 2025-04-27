<?php

// Custom Exception Class
class FileUploadException extends Exception {}

function uploadFile($file) {
    $uploadDirectory = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';
    $filePath = $uploadDirectory . basename($file['name']);

    try {
        // Check for upload errors
        if ($file['error'] !== UPLOAD_ERR_OK) {
            throw new FileUploadException('Upload error code: ' . $file['error']);
        }

        // Attempt to move the uploaded file
        $move = move_uploaded_file($file['tmp_name'], $filePath);

        if (!$move) {
            throw new FileUploadException('Failed to move uploaded file.');
        }

        echo 'Upload Complete!';

    } catch (FileUploadException $e) {
        // Handle specific file upload error
        echo 'File upload failed: ' . $e->getMessage();
    } catch (Exception $e) {
        // Handle general exceptions
        echo 'An error occurred: ' . $e->getMessage();
    }
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_FILES['file'])) {
    uploadFile($_FILES['file']);
}