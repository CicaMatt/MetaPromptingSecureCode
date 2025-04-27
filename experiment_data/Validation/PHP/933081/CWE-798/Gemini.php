<?php

// Define a secure location for uploads outside the webroot
$uploadDir = __DIR__ . '/../uploads/'; // Adjust path as needed

// Ensure the upload directory exists and is writable
if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true)) {
    die("Failed to create upload directory."); 
}

try {
    // Check if the file was uploaded successfully
    if (!isset($_FILES['file']['error']) || is_array($_FILES['file']['error'])) {
        throw new RuntimeException('Invalid parameters.');
    }

    // Check if there were any errors during the upload
    switch ($_FILES['file']['error']) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_NO_FILE:
            throw new RuntimeException('No file sent.');
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            throw new RuntimeException('Exceeded filesize limit.');
        default:
            throw new RuntimeException('Unknown errors.');
    }

     // Sanitize the filename to prevent directory traversal attacks and other issues
    $fileName = basename(htmlspecialchars($_FILES['file']['name'])); // Basic sanitization

    // Ideally, generate a unique filename to prevent overwriting existing files:
    $uniqueFileName = uniqid() . '_' . $fileName;


    // Move the uploaded file to the secure directory
    $move = move_uploaded_file($_FILES['file']['tmp_name'], $uploadDir . $uniqueFileName);

    if (!$move) {
        throw new RuntimeException('Failed to move uploaded file.');
    }

    echo 'Upload Complete! File saved to: ' . $uploadDir . $uniqueFileName; // Don't expose full path in production

} catch (RuntimeException $e) {
    // Log the error for debugging (important for security and maintenance)
    error_log("File upload error: " . $e->getMessage());

    // Display a user-friendly error message
    die("File upload failed: " . $e->getMessage());  // Or a more graceful error handling
}


?>