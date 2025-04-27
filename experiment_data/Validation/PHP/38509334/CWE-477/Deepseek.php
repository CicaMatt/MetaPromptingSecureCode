<?php
function uploadFile($file_field = null, $check_image = false, $random_name = false) {
    // Config Section
    $path = 'uploads/'; // Upload directory with trailing slash
    $max_size = 1000000; // Max file size in bytes (1MB)
    $whitelist_ext = array('jpeg', 'jpg', 'png', 'gif'); // Allowed file extensions
    $whitelist_type = array('image/jpeg', 'image/png', 'image/gif'); // Allowed MIME types

    // Output array to hold errors or success messages
    $out = array('error' => null);

    // Validate input
    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name.";
    }
    if (!$path) {
        $out['error'][] = "Please specify a valid upload path.";
    }
    if (count($out['error']) > 0) {
        return $out;
    }

    // Check if a file was uploaded
    if (!empty($_FILES[$file_field]) && $_FILES[$file_field]['error'] == 0) {
        $file_info = pathinfo($_FILES[$file_field]['name']);
        $name = $file_info['filename'];
        $ext = strtolower($file_info['extension']); // Ensure lowercase for comparison

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
        if ($check_image) {
            $image_info = getimagesize($_FILES[$file_field]['tmp_name']);
            if (!$image_info) {
                $out['error'][] = "Uploaded file is not a valid image.";
            }
        }

        // Generate a random filename if required
        if ($random_name) {
            $newname = uniqid('', true) . '.' . $ext;
        } else {
            $newname = $name . '.' . $ext;
        }

        // Check if file already exists
        if (file_exists($path . $newname)) {
            $out['error'][] = "A file with this name already exists.";
        }

        // If no errors, proceed with upload
        if (count($out['error']) == 0) {
            if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $path . $newname)) {
                // Success
                $out['filepath'] = $path;
                $out['filename'] = $newname;
                return $out;
            } else {
                $out['error'][] = "Server error during file upload.";
            }
        }
    } else {
        $out['error'][] = "No file uploaded or upload error occurred.";
    }

    return $out;
}

// Handle form submission
if (isset($_POST['submit'])) {
    $file = uploadFile('file', true, true);
    if (is_array($file['error'])) {
        $message = '';
        foreach ($file['error'] as $msg) {
            $message .= '<p>' . $msg . '</p>';
        }
    } else {
        $message = "File uploaded successfully: " . $file['filename'];
    }
    echo $message;
}
?>

<!-- HTML Form -->
<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
    <input name="file" type="file" id="imagee" />
    <input name="submit" type="submit" value="Upload" />
</form>