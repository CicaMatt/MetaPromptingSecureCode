<?php

// Configuration (store sensitive data outside of the webroot and protect this file)
require_once __DIR__ . '/../config.php'; // Example path; adjust as needed

// Define custom exceptions
class FileUploadException extends Exception {}
class InvalidFileTypeException extends FileUploadException {}
class FileStorageException extends FileUploadException {}

try {
    // Check if file was uploaded without errors
    if (empty($_FILES['file']['tmp_name']) || $_FILES['file']['error'] !== UPLOAD_ERR_OK) {
        throw new FileUploadException('File upload failed: ' . $_FILES['file']['error']);
    }

    // Validate file type (example: only allow images)
    $allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif']; 
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mimeType = finfo_file($finfo, $_FILES['file']['tmp_name']);
    finfo_close($finfo);

    if (!in_array($mimeType, $allowedMimeTypes)) {
        throw new InvalidFileTypeException('Invalid file type: ' . $mimeType);
    }

    // Sanitize filename to prevent directory traversal and other issues
    $filename = basename($_FILES['file']['name']); 
    $filename = preg_replace('/[^a-zA-Z0-9.\-_]/', '_', $filename); // Allow only alphanumeric characters, dots, hyphens, and underscores

    // Use a randomly generated filename to prevent overwriting existing files
    $uniqueFilename = uniqid() . '_' . $filename;

    $uploadDir = __DIR__ . '/../uploads/'; // Assuming 'uploads' is outside the webroot
    if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true) && !is_writable($uploadDir)) {
        throw new FileStorageException('Failed to create or write to upload directory.');
    }

    $destination = $uploadDir . $uniqueFilename;

    if (!move_uploaded_file($_FILES['file']['tmp_name'], $destination)) {
        throw new FileStorageException('Failed to move uploaded file.');
    }

    echo 'Upload Complete! File saved as: ' . $uniqueFilename;

} catch (InvalidFileTypeException $e) {
    error_log("Invalid file upload: " . $e->getMessage()); // Log the error for debugging
    http_response_code(400); // Indicate bad request
    echo 'Invalid file type.'; // User-friendly message
} catch (FileStorageException $e) {
    error_log("Storage error: " . $e->getMessage()); // Log the error for debugging
    http_response_code(500); // Indicate server error
    echo 'File storage failed.'; // User-friendly message
} catch (FileUploadException $e) {
    error_log("File upload error: " . $e->getMessage()); // Log the error for debugging
    http_response_code(500); // Indicate server error
    echo 'File upload failed.';  // User-friendly message
}

?>
