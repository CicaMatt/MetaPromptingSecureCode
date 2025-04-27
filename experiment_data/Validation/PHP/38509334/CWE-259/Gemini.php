<?php

// Configuration (store these securely, e.g., environment variables)
$upload_path = 'uploads/'; // Ensure this directory exists and is outside the webroot
$max_file_size = 1000000; // 1MB
$allowed_extensions = ['jpg', 'jpeg', 'png', 'gif'];
$allowed_mime_types = ['image/jpeg', 'image/png', 'image/gif'];


function uploadFile($file_field) {
    global $upload_path, $max_file_size, $allowed_extensions, $allowed_mime_types;

    $errors = [];

    if (empty($_FILES[$file_field]['name'])) {
        $errors[] = "No file uploaded.";
        return $errors;
    }

    // Input validation
    $file_name = $_FILES[$file_field]['name'];
    $file_tmp = $_FILES[$file_field]['tmp_name'];
    $file_size = $_FILES[$file_field]['size'];
    $file_type = $_FILES[$file_field]['type']; 
    $file_ext = strtolower(pathinfo($file_name, PATHINFO_EXTENSION));

    if (!in_array($file_ext, $allowed_extensions)) {
        $errors[] = "Invalid file extension.";
    }

    if (!in_array($file_type, $allowed_mime_types)) {
        $errors[] = "Invalid file type.";
    }
    

    if ($file_size > $max_file_size) {
        $errors[] = "File size exceeds limit.";
    }


    // Image validation: important step to prevent disguised files 
    $image_info = getimagesize($file_tmp);
    if (!$image_info || !in_array($image_info[2], [IMAGETYPE_JPEG, IMAGETYPE_PNG, IMAGETYPE_GIF])) {
        $errors[] = "Uploaded file is not a valid image.";
    }



    if (empty($errors)) {
        $new_file_name = bin2hex(random_bytes(16)) . '.' . $file_ext; // Cryptographically secure random filename
        $destination = $upload_path . $new_file_name;

        if (move_uploaded_file($file_tmp, $destination)) {
           // Optionally, re-process the image using GD to further mitigate risks. Example:
           // $image = imagecreatefromjpeg($destination);
           // imagejpeg($image, $destination, 90); // Save with quality 90
           // imagedestroy($image);

            return ['filepath' => $destination, 'filename' => $new_file_name];
        } else {
            $errors[] = "Error moving uploaded file.";
        }
    }


    return $errors;

}

if (isset($_POST['submit'])) {
    $result = uploadFile('file');

    if (is_array($result)) {  // Error array returned
        $message = '';
        foreach ($result as $error) {
            $message .= "<p>$error</p>";
        }
    } else {
        $message = "File uploaded successfully: " . $result['filename'];
    }

    echo $message;
}
?>


<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="file" id="file">
    <input type="submit" name="submit" value="Upload">
</form>