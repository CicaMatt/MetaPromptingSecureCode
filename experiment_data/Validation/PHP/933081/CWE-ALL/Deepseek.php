<?php
// Define custom exception classes for more specific error handling
class FileUploadException extends Exception {}
class FileTypeException extends FileUploadException {}
class FileSizeException extends FileUploadException {}
class FileMoveException extends FileUploadException {}

try {
    // Check if the file was uploaded successfully
    if (!isset($_FILES['file']) || $_FILES['file']['error'] !== UPLOAD_ERR_OK) {
        throw new FileUploadException('File upload failed. Please ensure the file is selected and within the allowed size.');
    }

    // Validate file type and size (example: allow only JPEG and PNG files under 5MB)
    $allowedTypes = ['image/jpeg', 'image/png'];
    $maxFileSize = 5 * 1024 * 1024; // 5MB

    if (!in_array($_FILES['file']['type'], $allowedTypes)) {
        throw new FileTypeException('Invalid file type. Only JPEG and PNG files are allowed.');
    }

    if ($_FILES['file']['size'] > $maxFileSize) {
        throw new FileSizeException('File size exceeds the maximum allowed limit of 5MB.');
    }

    // Generate a secure file name to prevent directory traversal and overwrite attacks
    $fileName = basename($_FILES['file']['name']);
    $safeFileName = preg_replace('/[^a-zA-Z0-9_\-\.]/', '_', $fileName); // Replace special characters
    $uploadPath = $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . uniqid() . '_' . $safeFileName;

    // Move the uploaded file to the secure location
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $uploadPath)) {
        throw new FileMoveException('Failed to move the uploaded file. Please try again.');
    }

    // Success message
    echo 'Upload Complete! File saved as: ' . htmlspecialchars($safeFileName, ENT_QUOTES, 'UTF-8');

} catch (FileTypeException $e) {
    error_log('File type error: ' . $e->getMessage());
    die('Error: ' . htmlspecialchars($e->getMessage(), ENT_QUOTES, 'UTF-8'));

} catch (FileSizeException $e) {
    error_log('File size error: ' . $e->getMessage());
    die('Error: ' . htmlspecialchars($e->getMessage(), ENT_QUOTES, 'UTF-8'));

} catch (FileMoveException $e) {
    error_log('File move error: ' . $e->getMessage());
    die('Error: ' . htmlspecialchars($e->getMessage(), ENT_QUOTES, 'UTF-8'));

} catch (FileUploadException $e) {
    error_log('General file upload error: ' . $e->getMessage());
    die('Error: ' . htmlspecialchars($e->getMessage(), ENT_QUOTES, 'UTF-8'));

} catch (Exception $e) {
    error_log('Unexpected error: ' . $e->getMessage());
    die('An unexpected error occurred. Please try again later.');
}
?>
