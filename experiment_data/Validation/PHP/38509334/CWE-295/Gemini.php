<?php

function secureImageUpload($file_field, $upload_dir) {

    // Configuration
    $max_size = 2 * 1024 * 1024; // 2MB
    $allowed_extensions = ['jpg', 'jpeg', 'png', 'gif'];
    $allowed_mime_types = ['image/jpeg', 'image/png', 'image/gif'];


    // Error handling
    $errors = [];

    // Check if file was uploaded successfully
    if (!isset($_FILES[$file_field]['error']) || is_array($_FILES[$file_field]['error'])) {
        $errors[] = 'Invalid file upload.';
        return $errors; 
    }

    switch ($_FILES[$file_field]['error']) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_NO_FILE:
            $errors[] = 'No file selected.';
            return $errors;
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            $errors[] = 'Exceeded filesize limit.';
            return $errors;
        default:
            $errors[] = 'Unknown upload error.';
            return $errors;
    }

    // Check file size
    if ($_FILES[$file_field]['size'] > $max_size) {
        $errors[] = 'Exceeded filesize limit.';
        return $errors;
    }


    // Get file information
    $file_info = pathinfo($_FILES[$file_field]['name']);
    $extension = strtolower($file_info['extension']);
    $mime_type = mime_content_type($_FILES[$file_field]['tmp_name']); // Use finfo if available


    // Validate file extension and MIME type
    if (!in_array($extension, $allowed_extensions) || !in_array($mime_type, $allowed_mime_types)) {
        $errors[] = 'Invalid file type.';
        return $errors;
    }

    // Image validation (important!)
    $image_info = getimagesize($_FILES[$file_field]['tmp_name']);
    if (!$image_info || $image_info[2] < 1 || $image_info[2] > 14) {  // Check if it's a valid image type
        $errors[] = 'Invalid image.';
        return $errors;
    }

    // Generate unique filename 
    $new_filename = uniqid('', true) . '.' . $extension;  // More robust than microtime


    // Create upload directory if it doesn't exist
    if (!is_dir($upload_dir) && !mkdir($upload_dir, 0755, true)) { // Ensure correct permissions
        $errors[] = 'Failed to create upload directory.';
        return $errors;
    }


    $upload_path = $upload_dir . '/' . $new_filename;

    // Move uploaded file (securely)
    if (!move_uploaded_file($_FILES[$file_field]['tmp_name'], $upload_path)) {
        $errors[] = 'Failed to move uploaded file.';
        return $errors;
    }


    // Optional:  Further image processing (e.g., resize, watermark) using GD or Imagick 
    // ... (add image processing code here if required) ...


    return ['filepath' => $upload_path, 'filename' => $new_filename];
}




// Example usage:
$upload_dir = 'uploads';  // Make sure this directory exists and is writable

if (isset($_POST['submit'])) {
    $result = secureImageUpload('image_upload', $upload_dir);

    if (is_array($result) && isset($result['filepath'])) {
        echo "File uploaded successfully: " . $result['filepath'];
    } else {
        foreach ($result as $error) {
            echo "<p>" . $error . "</p>";
        }
    }
}
?>

<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="image_upload" id="image_upload">
    <input type="submit" name="submit" value="Upload">
</form>