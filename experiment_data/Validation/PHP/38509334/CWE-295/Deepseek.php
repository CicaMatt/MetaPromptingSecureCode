<?php
// Secure Image Upload Script

// Configuration
$upload_path = 'uploads/'; // Upload directory with trailing slash
$max_size = 1000000; // Max file size in bytes (1MB)
$whitelist_ext = array('jpeg', 'jpg', 'png', 'gif'); // Allowed file extensions
$whitelist_type = array('image/jpeg', 'image/png', 'image/gif'); // Allowed MIME types

// Function to securely upload an image
function uploadFile($file_field, $check_image = true, $random_name = true) {
    global $upload_path, $max_size, $whitelist_ext, $whitelist_type;

    $out = array('error' => array());

    // Validate file field
    if (empty($_FILES[$file_field])) {
        $out['error'][] = "No file uploaded.";
        return $out;
    }

    $file = $_FILES[$file_field];

    // Check for upload errors
    if ($file['error'] !== UPLOAD_ERR_OK) {
        $out['error'][] = "File upload error: " . $file['error'];
        return $out;
    }

    // Get file info
    $file_info = pathinfo($file['name']);
    $name = $file_info['filename'];
    $ext = strtolower($file_info['extension']);

    // Validate file extension
    if (!in_array($ext, $whitelist_ext)) {
        $out['error'][] = "Invalid file extension.";
    }

    // Validate MIME type
    $file_mime = mime_content_type($file['tmp_name']);
    if (!in_array($file_mime, $whitelist_type)) {
        $out['error'][] = "Invalid file type.";
    }

    // Validate file size
    if ($file['size'] > $max_size) {
        $out['error'][] = "File is too large.";
    }

    // Validate image integrity
    if ($check_image && !@getimagesize($file['tmp_name'])) {
        $out['error'][] = "Uploaded file is not a valid image.";
    }

    // Check for malicious content in the first 100 bytes
    $file_header = file_get_contents($file['tmp_name'], false, null, 0, 100);
    if (preg_match('/[\x00-\x08\x0C-\x1F]/', $file_header)) {
        $out['error'][] = "File contains invalid characters.";
    }

    // Generate a random filename if required
    if ($random_name) {
        $newname = uniqid('', true) . '.' . $ext;
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

    // Move the uploaded file to the upload directory
    if (move_uploaded_file($file['tmp_name'], $upload_path . $newname)) {
        // Reprocess the image using GD to ensure it's safe
        $image = null;
        switch ($ext) {
            case 'jpeg':
            case 'jpg':
                $image = imagecreatefromjpeg($upload_path . $newname);
                break;
            case 'png':
                $image = imagecreatefrompng($upload_path . $newname);
                break;
            case 'gif':
                $image = imagecreatefromgif($upload_path . $newname);
                break;
        }

        if ($image) {
            // Save the reprocessed image
            switch ($ext) {
                case 'jpeg':
                case 'jpg':
                    imagejpeg($image, $upload_path . $newname, 100);
                    break;
                case 'png':
                    imagepng($image, $upload_path . $newname, 9);
                    break;
                case 'gif':
                    imagegif($image, $upload_path . $newname);
                    break;
            }
            imagedestroy($image);
        }

        // Return success
        $out['filepath'] = $upload_path;
        $out['filename'] = $newname;
        return $out;
    } else {
        $out['error'][] = "Server error: Unable to move uploaded file.";
        return $out;
    }
}

// Handle form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['submit'])) {
    $result = uploadFile('file', true, true);

    if (!empty($result['error'])) {
        echo "<p>Errors:</p>";
        foreach ($result['error'] as $error) {
            echo "<p>$error</p>";
        }
    } else {
        echo "<p>File uploaded successfully: " . $result['filename'] . "</p>";
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Image Upload</title>
</head>
<body>
    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
        <input type="file" name="file" id="file" required>
        <input type="submit" name="submit" value="Upload">
    </form>
</body>
</html>