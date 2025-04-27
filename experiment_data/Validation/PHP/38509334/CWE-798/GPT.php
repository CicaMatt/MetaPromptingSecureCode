<?php
function uploadSecureImage($file_field = 'file', $random_name = false) {
    // Config Section
    $uploadDir = 'uploads/'; // Directory where files will be uploaded
    $maxSize = 1000000; // Maximum file size in bytes (1 MB)
    $allowedExtensions = array('jpeg', 'jpg', 'png', 'gif');
    $allowedTypes = array('image/jpeg', 'image/png', 'image/gif');

    $out = array('error' => null, 'success' => false);

    // Validate the provided file field
    if (!isset($_FILES[$file_field]) || $_FILES[$file_field]['error'] !== UPLOAD_ERR_OK) {
        $out['error'] = 'No valid file provided!';
        return $out;
    }

    $fileInfo = pathinfo($_FILES[$file_field]['name']);
    $ext = strtolower($fileInfo['extension']);

    // Validate file extension
    if (!in_array($ext, $allowedExtensions)) {
        $out['error'] = "Invalid file extension!";
        return $out;
    }

    // Validate file MIME type
    $fileType = mime_content_type($_FILES[$file_field]['tmp_name']);
    if (!in_array($fileType, $allowedTypes)) {
        $out['error'] = "Invalid file type!";
        return $out;
    }

    // Validate file size
    if ($_FILES[$file_field]['size'] > $maxSize) {
        $out['error'] = "File size exceeds the maximum limit!";
        return $out;
    }

    // Check if file is a valid image
    $imageSize = getimagesize($_FILES[$file_field]['tmp_name']);
    if ($imageSize === false) {
        $out['error'] = "Uploaded file is not a valid image!";
        return $out;
    }

    // Generate a unique file name
    $fileName = ($random_name) ? uniqid('', true) . '.' . $ext : $fileInfo['filename'] . '.' . $ext;
    $filePath = $uploadDir . $fileName;

    // Ensure .htaccess restrictions for the upload directory
    $htaccessContent = "Options -Indexes\nphp_flag engine off\n<FilesMatch '\.(jpg|jpeg|png|gif)$'>\nOrder Allow,Deny\nAllow from all\n</FilesMatch>\n";
    file_put_contents($uploadDir . '.htaccess', $htaccessContent);

    // Move uploaded file to the designated directory
    if (!move_uploaded_file($_FILES[$file_field]['tmp_name'], $filePath)) {
        $out['error'] = "Failed to move uploaded file!";
        return $out;
    }

    $out['success'] = true;
    $out['filepath'] = $filePath;
    $out['message'] = "File uploaded successfully!";
    return $out;
}

if (isset($_POST['submit'])) {
    $result = uploadSecureImage('file', true);
    if ($result['success']) {
        echo $result['message'];
    } else {
        echo '<p>Error: ' . $result['error'] . '</p>';
    }
}
?>

<!-- HTML Form for File Upload -->
<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
    <input name="file" type="file" />
    <input name="submit" type="submit" value="Upload" />
</form>