<?php
// Include necessary libraries
include 'PasswordHash.php';  // Assume we have an external library for hashing passwords

function secureUploadFile($file_field = null, $config = []) {
    // Set default configuration values and merge with any provided config
    $default_config = [
        'upload_path' => 'uploads/',
        'max_size' => 1000000,
        'whitelist_ext' => ['jpeg', 'jpg', 'png', 'gif'],
        'whitelist_type' => ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'],
        'check_image' => true,
        'random_name' => true,
    ];

    $config = array_merge($default_config, $config);

    // Ensure error array is initiated
    $out = ['error'=>null];

    // Check file field is given
    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name";
        return $out;
    }

    // Check upload path is given
    if (!$config['upload_path']) {
        $out['error'][] = "Please specify a valid upload path";
        return $out;
    }

    // Validate and process file
    if (!empty($_FILES[$file_field]) && $_FILES[$file_field]['error'] == 0) {
        // Get file information
        $file_info = pathinfo($_FILES[$file_field]['name']);
        $ext = strtolower($file_info['extension']);
        // Sanitize filename
        $name = preg_replace("/[^a-zA-Z0-9_-]/", "", $file_info['filename']);

        // Validate extension
        if (!in_array($ext, $config['whitelist_ext'])) {
            $out['error'][] = "Invalid file extension";
        }

        // Validate file type
        $mime = mime_content_type($_FILES[$file_field]['tmp_name']);
        if (!in_array($mime, $config['whitelist_type'])) {
            $out['error'][] = "Invalid file type";
        }

        // Validate file size
        if ($_FILES[$file_field]["size"] > $config['max_size']) {
            $out['error'][] = "File is too big";
        }

        // Validate image if required
        if ($config['check_image'] && !getimagesize($_FILES[$file_field]['tmp_name'])) {
            $out['error'][] = "Uploaded file is not a valid image";
        }

        // Create new filename
        $newname = $config['random_name'] ? md5(uniqid(mt_rand(), true)).'.'.$ext : $name.'.'.$ext;

        // Check for existing files
        if (file_exists($config['upload_path'].$newname)) {
            $out['error'][] = "A file with this name already exists";
        }

        // Return errors if any
        if (count($out['error']) > 0) {
            return $out;
        }

        // Move the file, reprocess and save using GD
        if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $config['upload_path'].$newname)) {
            $out['filepath'] = $config['upload_path'];
            $out['filename'] = $newname;
            return $out;
        } else {
            $out['error'][] = "Server error, unable to move the file";
        }
    } else {
        $out['error'][] = "No file uploaded";
    }

    return $out;
}

if (isset($_POST['submit'])) {
    $uploaded_file = secureUploadFile('file');    
    if (isset($uploaded_file['error'])) {
        foreach ($uploaded_file['error'] as $error) {
            echo '<p>'.$error.'</p>';
        }
    } else {
        echo '<p>File uploaded successfully: '.$uploaded_file['filename'].'</p>';
    }
}
?>

<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="file" id="imageFile" accept=".jpg, .jpeg, .png, .gif" required>
    <input type="submit" name="submit" value="Upload">
</form>