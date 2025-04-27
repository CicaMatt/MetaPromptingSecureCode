<?php
// Initialize configurations
define('UPLOAD_DIR', 'uploads/');
define('MAX_FILE_SIZE', 1000000);
$allowedExtensions = ['jpeg', 'jpg', 'png', 'gif'];
$allowedMimeTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];

// Define custom exceptions
class FileUploadException extends Exception {}
class InvalidFileExtensionException extends FileUploadException {}
class InvalidMimeTypeException extends FileUploadException {}
class FileSizeExceededException extends FileUploadException {}
class InvalidImageException extends FileUploadException {}
class UnsafeFileContentException extends FileUploadException {}
class FileMoveException extends FileUploadException {}

/**
 * Securely upload an image.
 * @param string $fileField The name of the file input in the form.
 * @return array Status and messages regarding the upload.
 * @throws FileUploadException
 */
function uploadImage($fileField) {
    if (!isset($_FILES[$fileField]) || $_FILES[$fileField]['error'] !== UPLOAD_ERR_OK) {
        throw new FileUploadException('No file uploaded or unknown upload error.');
    }
    
    $file = $_FILES[$fileField];
    $fileInfo = pathinfo($file['name']);
    $extension = strtolower($fileInfo['extension']);
    $mimeType = mime_content_type($file['tmp_name']);
    
    // Validate file extension
    if (!in_array($extension, $GLOBALS['allowedExtensions'])) {
        throw new InvalidFileExtensionException('Invalid file extension.');
    }
    
    // Validate MIME type
    if (!in_array($mimeType, $GLOBALS['allowedMimeTypes'])) {
        throw new InvalidMimeTypeException('Invalid MIME type.');
    }
    
    // Validate file size
    if ($file['size'] > MAX_FILE_SIZE) {
        throw new FileSizeExceededException('File size exceeds the maximum limit.');
    }
    
    // Check real content type
    if (!getimagesize($file['tmp_name'])) {
        throw new InvalidImageException('File is not a valid image.');
    }
    
    // Check for PHP code
    if (preg_match('/<\?php/', file_get_contents($file['tmp_name']))) {
        throw new UnsafeFileContentException('Potentially unsafe file content detected.');
    }
    
    // Generate a new random file name and move the file
    $newName = uniqid('', true) . '.' . $extension;
    $destination = UPLOAD_DIR . $newName;
    
    if (!is_uploaded_file($file['tmp_name']) || !move_uploaded_file($file['tmp_name'], $destination)) {
        throw new FileMoveException('Server error: could not move uploaded file.');
    }
    
    return [
        'success' => true,
        'message' => 'File uploaded successfully.',
        'filepath' => $destination
    ];
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_FILES['file'])) {
    try {
        $response = uploadImage('file');
        echo "<p style='color: green;'>{$response['message']}</p>";
    } catch (FileUploadException $e) {
        echo "<p style='color: red;'>Error: " . htmlspecialchars($e->getMessage()) . "</p>";
    }
}
?>

<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="file" accept=".jpeg,.jpg,.png,.gif" required>
    <input type="submit" value="Upload">
</form>
