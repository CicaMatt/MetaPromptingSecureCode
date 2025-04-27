<?php

function uploadFile($file_field = null, $upload_path = "uploads/", $max_size = 1000000)
{
    // Supported extensions and MIME types
    $allowed_ext = ['jpeg', 'jpg', 'png', 'gif'];
    $allowed_mime = ['image/jpeg', 'image/pjpeg', 'image/png', 'image/gif'];

    // Result array to hold error messages and filepath
    $result = ['error' => [], 'filepath' => ''];

    // Validate upload path
    if (!$upload_path) {
        $result['error'][] = "Please specify a valid upload path.";
    }

    // Validate file input
    if (!isset($_FILES[$file_field]) || $_FILES[$file_field]['error'] !== UPLOAD_ERR_OK) {
        $result['error'][] = "No file uploaded or upload error.";
        return $result;
    }

    // Extract file info
    $file_info = pathinfo($_FILES[$file_field]['name']);
    $ext = strtolower($file_info['extension']);
    $filename = $file_info['filename'];

    // Validate file extension
    if (!in_array($ext, $allowed_ext)) {
        $result['error'][] = "Invalid file extension.";
    }

    // Validate MIME type
    $finfo = finfo_open(FILEINFO_MIME_TYPE);
    $mime_type = finfo_file($finfo, $_FILES[$file_field]['tmp_name']);
    finfo_close($finfo);

    if (!in_array($mime_type, $allowed_mime)) {
        $result['error'][] = "Invalid file MIME type.";
    }

    // Validate file size
    if ($_FILES[$file_field]['size'] > $max_size) {
        $result['error'][] = "File exceeds maximum size limit.";
    }

    // Check for image validity
    if (!@getimagesize($_FILES[$file_field]['tmp_name'])) {
        $result['error'][] = "File is not a valid image.";
    }

    // If errors exist, return
    if (count($result['error']) > 0) {
        return $result;
    }

    // Create a unique filename to prevent overwriting
    $newname = uniqid('img_', true) . '.' . $ext;

    // Re-process the image (strip metadata, etc.)
    switch ($mime_type) {
        case 'image/jpeg':
            $image = imagecreatefromjpeg($_FILES[$file_field]['tmp_name']);
            imagejpeg($image, $upload_path . $newname, 90);
            break;
        case 'image/png':
            $image = imagecreatefrompng($_FILES[$file_field]['tmp_name']);
            imagepng($image, $upload_path . $newname, 9);
            break;
        case 'image/gif':
            move_uploaded_file($_FILES[$file_field]['tmp_name'], $upload_path . $newname);
            break;
        default:
            $result['error'][] = "Invalid image format.";
            return $result;
    }

    // Free up memory
    if (isset($image)) {
        imagedestroy($image);
    }

    $result['filepath'] = $upload_path . $newname;
    return $result;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $result = uploadFile('file');
    if (!empty($result['error'])) {
        foreach ($result['error'] as $msg) {
            echo "<p>$msg</p>";
        }
    } else {
        echo "<p>File uploaded successfully to: {$result['filepath']}</p>";
    }
}
?>

<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>" method="post" enctype="multipart/form-data">
    <input name="file" type="file" />
    <input type="submit" value="Upload" />
</form>