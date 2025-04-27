<?php
// Ensure the upload directory is secure
$upload_dir = 'uploads/';
if (!is_dir($upload_dir)) {
    mkdir($upload_dir, 0755, true);
}

// Disable PHP execution in the upload directory
$htaccess_content = "php_flag engine off\n";
file_put_contents($upload_dir . '.htaccess', $htaccess_content);

function uploadFile($file_field = null, $check_image = false, $random_name = false) {
    // Config Section
    $path = 'uploads/'; // with trailing slash
    $max_size = 1000000; // 1MB
    $whitelist_ext = array('jpeg', 'jpg', 'png', 'gif');
    $whitelist_type = array('image/jpeg', 'image/jpg', 'image/png', 'image/gif');

    // The Validation
    $out = array('error' => null);

    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name";
    }

    if (!$path) {
        $out['error'][] = "Please specify a valid upload path";
    }

    if (count($out['error']) > 0) {
        return $out;
    }

    // Make sure that there is a file
    if ((!empty($_FILES[$file_field])) && ($_FILES[$file_field]['error'] == 0)) {
        // Get filename
        $file_info = pathinfo($_FILES[$file_field]['name']);
        $name = $file_info['filename'];
        $ext = strtolower($file_info['extension']);

        // Check file has the right extension
        if (!in_array($ext, $whitelist_ext)) {
            $out['error'][] = "Invalid file Extension";
        }

        // Check that the file is of the right type
        if (!in_array($_FILES[$file_field]["type"], $whitelist_type)) {
            $out['error'][] = "Invalid file Type";
        }

        // Check that the file is not too big
        if ($_FILES[$file_field]["size"] > $max_size) {
            $out['error'][] = "File is too big";
        }

        // If $check_image is set as true
        if ($check_image) {
            if (!getimagesize($_FILES[$file_field]['tmp_name'])) {
                $out['error'][] = "Uploaded file is not a valid image";
            }
        }

        // Check for malicious content in the first 100 bytes
        $file_content = file_get_contents($_FILES[$file_field]['tmp_name'], false, null, 0, 100);
        if (preg_match('/[\x00-\x08\x0C-\x1F]/', $file_content)) {
            $out['error'][] = "File contains malicious content";
        }

        // Create full filename including path
        if ($random_name) {
            // Generate random filename
            $tmp = str_replace(array('.', ' '), array('', ''), microtime());

            if (!$tmp || $tmp == '') {
                $out['error'][] = "File must have a name";
            }
            $newname = $tmp . '.' . $ext;
        } else {
            $newname = $name . '.' . $ext;
        }

        // Check if file already exists on server
        if (file_exists($path . $newname)) {
            $out['error'][] = "A file with this name already exists";
        }

        if (count($out['error']) > 0) {
            // The file has not correctly validated
            return $out;
        }

        // Re-process the image using GD
        if ($check_image) {
            $image = null;
            switch ($ext) {
                case 'jpeg':
                case 'jpg':
                    $image = imagecreatefromjpeg($_FILES[$file_field]['tmp_name']);
                    break;
                case 'png':
                    $image = imagecreatefrompng($_FILES[$file_field]['tmp_name']);
                    break;
                case 'gif':
                    $image = imagecreatefromgif($_FILES[$file_field]['tmp_name']);
                    break;
            }

            if ($image) {
                $new_image_path = $path . $newname;
                switch ($ext) {
                    case 'jpeg':
                    case 'jpg':
                        imagejpeg($image, $new_image_path, 100);
                        break;
                    case 'png':
                        imagepng($image, $new_image_path, 9);
                        break;
                    case 'gif':
                        imagegif($image, $new_image_path);
                        break;
                }
                imagedestroy($image);
            } else {
                $out['error'][] = "Failed to re-process the image";
                return $out;
            }
        } else {
            if (!move_uploaded_file($_FILES[$file_field]['tmp_name'], $path . $newname)) {
                $out['error'][] = "Server Error!";
                return $out;
            }
        }

        // Success
        $out['filepath'] = $path;
        $out['filename'] = $newname;
        return $out;
    } else {
        $out['error'][] = "No file uploaded";
        return $out;
    }
}

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
<html>
<head>
    <title>Secure Image Upload</title>
</head>
<body>
    <form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data" name="form1" id="form1">
        <input name="file" type="file" id="imagee" />
        <input name="submit" type="submit" value="Upload" />
    </form>
</body>
</html>