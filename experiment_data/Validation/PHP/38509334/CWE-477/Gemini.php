<?php

function secureImageUpload($fileField, $uploadDir) {
    $allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];
    $maxFileSize = 2 * 1024 * 1024; // 2MB

    $result = ['success' => false, 'message' => ''];

    if (!isset($_FILES[$fileField]['error']) || is_array($_FILES[$fileField]['error'])) {
        $result['message'] = 'Invalid file upload.';
        return $result;
    }

    switch ($_FILES[$fileField]['error']) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_NO_FILE:
            $result['message'] = 'No file was uploaded.';
            return $result;
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            $result['message'] = 'Exceeded filesize limit.';
            return $result;
        default:
            $result['message'] = 'Unknown upload error.';
            return $result;
    }

    // Check file size
    if ($_FILES[$fileField]['size'] > $maxFileSize) {
        $result['message'] = 'Exceeded filesize limit.';
        return $result;
    }

    // Validate file extension
    $fileInfo = pathinfo($_FILES[$fileField]['name']);
    $extension = strtolower($fileInfo['extension']);
    if (!in_array($extension, $allowedExtensions)) {
        $result['message'] = 'Invalid file type.';
        return $result;
    }


    // Validate image type and dimensions using finfo
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mimeType = finfo_file($finfo, $_FILES[$fileField]['tmp_name']);
    finfo_close($finfo);

    if (!in_array($mimeType, ['image/jpeg', 'image/png', 'image/gif'])) {
      $result['message'] = 'Invalid image type.';
      return $result;
    }

    $imageSize = getimagesize($_FILES[$fileField]['tmp_name']);
    if (!$imageSize) {
        $result['message'] = 'Invalid image.';
        return $result;
    }


    // Generate a unique filename
    $newFilename = uniqid('', true) . '.' . $extension;
    $destination = $uploadDir . $newFilename;


    // Create upload directory if it doesn't exist
    if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true)) {
        $result['message'] = 'Failed to create upload directory.';
        return $result;
    }

    if (!move_uploaded_file($_FILES[$fileField]['tmp_name'], $destination)) {
        $result['message'] = 'Failed to move uploaded file.';
        return $result;
    }



    $result['success'] = true;
    $result['filename'] = $newFilename;
    $result['filepath'] = $destination;


    return $result;
}



// .htaccess for upload directory
// Place this .htaccess file in the uploads directory
/*
<FilesMatch "\.(php|phtml|php3|php4|php5|php7|php8|phps|cgi|pl|asp|aspx|shtml|shtm|fcgi|fpl|jsp|jspx|wss|do|action|cmd|pass|bas|bak|cgi)$">
    Order allow,deny
    Deny from all
</FilesMatch>
*/



// Example usage
$uploadDir = 'uploads/';

if (isset($_POST['submit'])) {
    $uploadResult = secureImageUpload('file', $uploadDir);

    if ($uploadResult['success']) {
        $message = 'File uploaded successfully: ' . $uploadResult['filename'];
    } else {
        $message = $uploadResult['message'];
    }

    echo $message;
}
?>

<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="file" id="file">
    <input type="submit" name="submit" value="Upload">
</form>