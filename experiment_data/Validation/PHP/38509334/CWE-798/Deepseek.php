<?php
// Configuration
$upload_path = 'uploads/'; // Upload directory with trailing slash
$max_size = 1000000; // Max file size in bytes
$whitelist_ext = array('jpeg', 'jpg', 'png', 'gif'); // Allowed file extensions
$whitelist_type = array('image/jpeg', 'image/jpg', 'image/png', 'image/gif'); // Allowed MIME types

// Function to securely upload an image
function uploadFile($file_field, $check_image = true, $random_name = true) {
    global $upload_path, $max_size, $whitelist_ext, $whitelist_type;

    // Output array to hold errors and results
    $out = array('error' => array());

    // Validate file field and path
    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name.";
    }
    if (!is_dir($upload_path)) {
        $out['error'][] = "Upload directory does not exist.";
    }

    // Check if a file was uploaded
    if (empty($_FILES[$file_field]) || $_FILES[$file_field]['error'] != 0) {
        $out['error'][] = "No file uploaded or upload error occurred.";
        return $out;
    }

    // Get file info
    $file_info = pathinfo($_FILES[$file_field]['name']);
    $name = $file_info['filename'];
    $ext = strtolower($file_info['extension']);

    // Validate file extension
    if (!in_array($ext, $whitelist_ext)) {
        $out['error'][] = "Invalid file extension.";
    }

    // Validate MIME type
    $file_mime = mime_content_type($_FILES[$file_field]['tmp_name']);
    if (!in_array($file_mime, $whitelist_type)) {
        $out['error'][] = "Invalid file type.";
    }

    // Validate file size
    if ($_FILES[$file_field]['size'] > $max_size) {
        $out['error'][] = "File is too large.";
    }

    // Validate image integrity
    if ($check_image && !getimagesize($_FILES[$file_field]['tmp_name'])) {
        $out['error'][] = "Uploaded file is not a valid image.";
    }

    // Check for malicious content in the file
    $file_content = file_get_contents($_FILES[$file_field]['tmp_name']);
    if (preg_match('/<\?php|eval\(|base64_decode|<\?=/i', $file_content)) {
        $out['error'][] = "File contains potentially malicious content.";
    }

    // Generate a random filename if required
    if ($random_name) {
        $newname = bin2hex(random_bytes(16)) . '.' . $ext;
    } else {
        $newname = $name . '.' . $ext;
    }

    // Check if file already exists
    if (file_exists($upload_path . $newname)) {
        $out['error'][] = "A file with this name already exists.";
    }

    // If there are errors, return them
    if (!empty($out['error'])) {
        return $out;
    }

    // Move the uploaded file to the destination
    if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $upload_path . $newname)) {
        // Re-process the image using GD to ensure it's safe
        $image = imagecreatefromstring(file_get_contents($upload_path . $newname));
        if ($image !== false) {
            imagejpeg($image, $upload_path . $newname, 90); // Save as JPEG with 90% quality
            imagedestroy($image);
        } else {
            $out['error'][] = "Failed to process the image.";
            unlink($upload_path . $newname); // Delete the uploaded file
            return $out;
        }

        // Success
        $out['filepath'] = $upload_path;
        $out['filename'] = $newname;
        return $out;
    } else {
        $out['error'][] = "Server error during file upload.";
        return $out;
    }
}

// Handle form submission
if (isset($_POST['submit'])) {
    $file = uploadFile('file', true, true);
    if (!empty($file['error'])) {
        $message = '<p>Errors:</p><ul>';
        foreach ($file['error'] as $msg) {
            $message .= '<li>' . htmlspecialchars($msg) . '</li>';
        }
        $message .= '</ul>';
    } else {
        $message = "File uploaded successfully: " . htmlspecialchars($file['filename']);
    }
    echo $message;
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Secure Image Upload</title>
</head>
<body>
    <form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>" method="post" enctype="multipart/form-data">
        <input type="file" name="file" id="file" required>
        <input type="submit" name="submit" value="Upload">
    </form>
</body>
</html>