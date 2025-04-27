<?php

// Configuration (ideally store these in a separate, protected config file)
$upload_path = 'uploads/';
$max_file_size = 1000000; // 1MB
$allowed_extensions = ['jpg', 'jpeg', 'png', 'gif'];
$allowed_mime_types = ['image/jpeg', 'image/png', 'image/gif'];

/**
 * Custom exception classes for better error handling
 */
class FileUploadException extends Exception {}
class FileValidationException extends Exception {}
class ImageProcessingException extends Exception {}
class FileMoveException extends Exception {}

function secure_upload_image($file_field) {
    global $upload_path, $max_file_size, $allowed_extensions, $allowed_mime_types;

    if (!isset($_FILES[$file_field]) || $_FILES[$file_field]['error'] !== UPLOAD_ERR_OK) {
        throw new FileUploadException('No file uploaded or upload error.');
    }

    $file_tmp = $_FILES[$file_field]['tmp_name'];
    $file_name = $_FILES[$file_field]['name'];
    $file_size = $_FILES[$file_field]['size'];
    $file_type = $_FILES[$file_field]['type'];

    if (!is_uploaded_file($file_tmp)) {
        throw new FileUploadException('Possible file upload attack.');
    }

    // File size check
    if ($file_size > $max_file_size) {
        throw new FileValidationException('File too large.');
    }

    // Extension and MIME type validation
    $file_ext = strtolower(pathinfo($file_name, PATHINFO_EXTENSION));
    if (!in_array($file_ext, $allowed_extensions) || !in_array($file_type, $allowed_mime_types)) {
        throw new FileValidationException('Invalid file type or extension.');
    }

    // Image validation and re-processing using GD
    if (!($image_data = @getimagesize($file_tmp))) {
        throw new FileValidationException('Uploaded file is not a valid image.');
    }

    try {
        switch ($image_data['mime']) {
            case 'image/jpeg':
                $image = imagecreatefromjpeg($file_tmp);
                break;
            case 'image/png':
                $image = imagecreatefrompng($file_tmp);
                break;
            case 'image/gif':
                $image = imagecreatefromgif($file_tmp);
                break;
            default:
                throw new ImageProcessingException('Unsupported image type.');
        }

        if (!$image) {
            throw new ImageProcessingException("Failed to create image from upload.");
        }

        // Generate a unique filename
        $new_file_name = bin2hex(random_bytes(16)) . '.' . $file_ext;
        $upload_destination = $upload_path . $new_file_name;

        // Save the re-processed image
        switch ($file_ext) {
            case 'jpg':
            case 'jpeg':
                imagejpeg($image, $upload_destination, 90);
                break;
            case 'png':
                imagepng($image, $upload_destination);
                break;
            case 'gif':
                imagegif($image, $upload_destination);
                break;
        }

        imagedestroy($image); // Free resources

    } catch (Exception $e) {
        throw new ImageProcessingException("Image processing error: " . $e->getMessage());
    }

    // Move the uploaded file
    if (!move_uploaded_file($file_tmp, $upload_destination)) {
        throw new FileMoveException('Failed to move uploaded file.');
    }

    return ['filepath' => $upload_path, 'filename' => $new_file_name];
}

// Handle form submission
if (isset($_POST['submit'])) {
    try {
        $result = secure_upload_image('file');
        echo "<p>File uploaded successfully: " . htmlspecialchars($result['filename']) . "</p>";
    } catch (FileUploadException | FileValidationException | ImageProcessingException | FileMoveException $e) {
        echo "<p>Error: " . htmlspecialchars($e->getMessage()) . "</p>";
    }
}
?>

<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="file" id="file">
    <input type="submit" name="submit" value="Upload">
</form>
