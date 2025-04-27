<?php

// Configuration (store these securely, e.g., environment variables)
$UPLOAD_PATH = 'uploads/';
$MAX_FILE_SIZE = 1000000; // 1MB
$ALLOWED_EXTENSIONS = ['jpg', 'jpeg', 'png', 'gif'];

// Disable PHP execution in the upload directory using .htaccess (create this file in the uploads directory)
// .htaccess contents:
// php_flag engine off

// Function to generate a random filename
function generateRandomFilename($extension) {
    return bin2hex(random_bytes(16)) . '.' . $extension;
}

// Function to securely upload and process images
function secureImageUpload($fileField) {
    global $UPLOAD_PATH, $MAX_FILE_SIZE, $ALLOWED_EXTENSIONS;

    $errors = [];

    if (empty($_FILES[$fileField]['name'])) {
        $errors[] = "No file uploaded.";
        return ['errors' => $errors]; 
    }

    $fileInfo = $_FILES[$fileField];
    $fileName = $fileInfo['name'];
    $tmpName = $fileInfo['tmp_name'];
    $fileSize = $fileInfo['size'];
    $fileError = $fileInfo['error'];

    if ($fileError !== UPLOAD_ERR_OK) {
        $errors[] = "Upload error: " . $fileError;
        return ['errors' => $errors];
    }

    $fileExtension = strtolower(pathinfo($fileName, PATHINFO_EXTENSION));

    if (!in_array($fileExtension, $ALLOWED_EXTENSIONS)) {
        $errors[] = "Invalid file extension. Allowed extensions: " . implode(", ", $ALLOWED_EXTENSIONS);
    }

    if ($fileSize > $MAX_FILE_SIZE) {
        $errors[] = "File size exceeds the limit of " . $MAX_FILE_SIZE / 1000 . "KB.";
    }

    $imageInfo = getimagesize($tmpName);
    if (!$imageInfo) {
        $errors[] = "Uploaded file is not a valid image.";
    }

    if (count($errors) > 0) {
        return ['errors' => $errors];
    }

    // Generate a new, random filename
    $newFileName = generateRandomFilename($fileExtension);
    $newFilePath = $UPLOAD_PATH . $newFileName;


    // Re-process the image using GD (recommended)
    $image = imagecreatefromstring(file_get_contents($tmpName)); //Create image from uploaded file. Important step.
    if ($image === false) {
        $errors[] = "Error creating image from uploaded file.";
        return ['errors' => $errors];
    }
    imagejpeg($image, $newFilePath, 90); // Save as JPEG with quality 90. Change to png, gif etc. if necessary.
    imagedestroy($image);  // Free up memory.

    if (!move_uploaded_file($tmpName, $newFilePath)) { //this move should technically fail since we processed the image above already
        $errors[] = "Error moving uploaded file.";
        return ['errors' => $errors];
    }
        
    return ['filepath' => $newFilePath, 'filename' => $newFileName];
}

if (isset($_POST['submit'])) {
    $uploadResult = secureImageUpload('file');

    if (isset($uploadResult['errors'])) {
        $message = implode("<br>", $uploadResult['errors']);
    } else {
        $message = "File uploaded successfully: " . $uploadResult['filename'];
    }

    echo $message;
}
?>

<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data">
    <input type="file" name="file" id="file">
    <input type="submit" name="submit" value="Upload">
</form>