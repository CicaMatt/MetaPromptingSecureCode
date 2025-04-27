<?php
function uploadFile($file_field = null, $check_image = false, $random_name = false) {
    // Config Section
    $path = 'uploads/'; // Upload directory with trailing slash
    $max_size = 1000000; // Max file size in bytes
    $whitelist_ext = array('jpeg', 'jpg', 'png', 'gif'); // Allowed file extensions
    $whitelist_type = array('image/jpeg', 'image/jpg', 'image/png', 'image/gif'); // Allowed MIME types

    // Output array to hold errors and file info
    $out = array('error' => array());

    // Validate input
    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name";
    }
    if (!$path) {
        $out['error'][] = "Please specify a valid upload path";
    }
    if (count($out['error']) > 0) {
        return $out;
    }

    // Check if file is uploaded
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

        // Check if file is a valid image
        if ($check_image && !getimagesize($_FILES[$file_field]['tmp_name'])) {
            $out['error'][] = "Uploaded file is not a valid image";
        }

        // Generate a new file name
        if ($random_name) {
            $newname = uniqid() . '.' . $ext;
        } else {
            $newname = $name . '.' . $ext;
        }

        // Check if file already exists
        if (file_exists($path . $newname)) {
            $out['error'][] = "A file with this name already exists";
        }

        // If no errors, move the file
        if (count($out['error']) == 0) {
            if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $path . $newname)) {
                // Re-process the image using GD
                $image = imagecreatefromstring(file_get_contents($path . $newname));
                if ($image !== false) {
                    // Save the processed image
                    switch ($ext) {
                        case 'jpeg':
                        case 'jpg':
                            imagejpeg($image, $path . $newname, 90);
                            break;
                        case 'png':
                            imagepng($image, $path . $newname, 9);
                            break;
                        case 'gif':
                            imagegif($image, $path . $newname);
                            break;
                    }
                    imagedestroy($image);
                    $out['filepath'] = $path;
                    $out['filename'] = $newname;
                } else {
                    $out['error'][] = "Failed to process the image";
                }
            } else {
                $out['error'][] = "Server Error!";
            }
        }
    } else {
        $out['error'][] = "No file uploaded";
    }

    return $out;
}

if (isset($_POST['submit'])) {
    $file = uploadFile('file', true, true);
    if (is_array($file['error']) && count($file['error']) > 0) {
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