<?php

// Configuration (ideally, store these in a secure location outside the webroot)
$uploadDir = $_SERVER['DOCUMENT_ROOT'] . '/uploads/';  // Ensure this directory exists and has appropriate permissions

try {
    // Input validation: Check if a file was uploaded and if there were any errors.
    if (!isset($_FILES['file']['error']) || is_array($_FILES['file']['error'])) {
        throw new RuntimeException('Invalid parameters.');
    }

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


    // Input validation: Sanitize the filename to prevent directory traversal attacks.
    $fileName = basename($_FILES['file']['name']); // Use basename for security
    $sanitizedFileName = preg_replace("/[^a-zA-Z0-9.\-_]/", "", $fileName); // Remove potentially harmful characters


    // Further validation (optional): Check file type/extension for whitelisting.  This helps prevent uploading malicious files.
    $allowedExtensions = ['jpg', 'jpeg', 'png', 'gif', 'pdf'];
    $fileExtension = strtolower(pathinfo($sanitizedFileName, PATHINFO_EXTENSION));
    if (!in_array($fileExtension, $allowedExtensions)) {
        throw new RuntimeException('Invalid file type.');
    }


    $targetFile = $uploadDir . $sanitizedFileName;


    // Check if file with the same sanitized name already exists and create a unique name if needed.
    if (file_exists($targetFile)) {
        $baseName = pathinfo($sanitizedFileName, PATHINFO_FILENAME);
        $extension = pathinfo($sanitizedFileName, PATHINFO_EXTENSION);
        $i = 1;
        while (file_exists($uploadDir . $baseName . '_' . $i . '.' . $extension)) {
            $i++;
        }
        $sanitizedFileName = $baseName . '_' . $i . '.' . $extension;
        $targetFile = $uploadDir . $sanitizedFileName;
    }

    if (!move_uploaded_file($_FILES['file']['tmp_name'], $targetFile)) { // finally, move the uploaded file
        throw new RuntimeException('Failed to move uploaded file.');
    }

    echo 'Upload Complete! File saved as: ' . $sanitizedFileName;


} catch (RuntimeException $e) {
    // Log the error for debugging. NEVER display raw error messages to users in production.
    error_log("File upload error: " . $e->getMessage()); 

    // Provide a user-friendly error message.
    die('File upload failed. Please try again.');  // Or redirect to an error page.
}


?>