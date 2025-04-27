<?php
// Configuration Section
$upload_path = 'uploads/'; // Ensure this directory is outside the web root
$max_size = 1000000; // 1MB
$whitelist_ext = array('jpeg', 'jpg', 'png', 'gif');
$whitelist_type = array('image/jpeg', 'image/jpg', 'image/png', 'image/gif');

// Function to securely upload an image
function uploadFile($file_field, $check_image = true, $random_name = true) {
    global $upload_path, $max_size, $whitelist_ext, $whitelist_type;

    $out = array('error' => null);

    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name";
    }

    if (!$upload_path) {
        $out['error'][] = "Please specify a valid upload path";
    }

    if (count($out['error']) > 0) {
        return $out;
    }

    if (!empty($_FILES[$file_field]) && $_FILES[$file_field]['error'] == 0) {
        $file_info = pathinfo($_FILES[$file_field]['name']);
        $name = $file_info['filename'];
        $ext = strtolower($file_info['extension']);

        // Check file extension
        if (!in_array($ext, $whitelist_ext)) {
            $out['error'][] = "Invalid file extension";
        }

        // Check MIME type
        $file_mime = mime_content_type($_FILES[$file_field]['tmp_name']);
        if (!in_array($file_mime, $whitelist_type)) {
            $out['error'][] = "Invalid file type";
        }

        // Check file size
        if ($_FILES[$file_field]['size'] > $max_size) {
            $out['error'][] = "File is too big";
        }

        // Check if the file is a valid image
        if ($check_image && !getimagesize($_FILES[$file_field]['tmp_name'])) {
            $out['error'][] = "Uploaded file is not a valid image";
        }

        // Check for malicious content in the first 100 bytes
        $file_content = file_get_contents($_FILES[$file_field]['tmp_name'], false, null, 0, 100);
        if (preg_match('/[\x00-\x08\x0B\x0C\x0E-\x1F]/', $file_content)) {
            $out['error'][] = "File contains malicious content";
        }

        // Generate a random filename
        if ($random_name) {
            $newname = bin2hex(random_bytes(16)) . '.' . $ext;
        } else {
            $newname = $name . '.' . $ext;
        }

        // Check if file already exists
        if (file_exists($upload_path . $newname)) {
            $out['error'][] = "A file with this name already exists";
        }

        if (count($out['error']) > 0) {
            return $out;
        }

        // Move the uploaded file to the upload directory
        if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $upload_path . $newname)) {
            // Re-process the image using GD to ensure it's safe
            $image = imagecreatefromstring(file_get_contents($upload_path . $newname));
            if ($image !== false) {
                imagejpeg($image, $upload_path . $newname, 90);
                imagedestroy($image);
            } else {
                unlink($upload_path . $newname);
                $out['error'][] = "Failed to re-process the image";
                return $out;
            }

            $out['filepath'] = $upload_path;
            $out['filename'] = $newname;
            return $out;
        } else {
            $out['error'][] = "Server Error!";
        }
    } else {
        $out['error'][] = "No file uploaded";
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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Secure Image Upload</title>
</head>
<body>
    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data" name="form1" id="form1">
        <input name="file" type="file" id="imagee" />
        <input name="submit" type="submit" value="Upload" />
    </form>
</body>
</html>