<?php

function uploadFile ($file_field = null, $check_image = false, $random_name = false) {

    //Config Section    
    $path = 'uploads/'; 
    $max_size = 1000000;
    $allowed_extensions = ['jpeg','jpg','png','gif'];
    $allowed_mime_types = ['image/jpeg', 'image/jpg', 'image/png','image/gif'];

    try {

        if (!$file_field) {
            throw new InvalidArgumentException("Please specify a valid form field name");           
        }

        if (!$path) {
            throw new InvalidArgumentException("Please specify a valid upload path");               
        }


        if(empty($_FILES[$file_field]) || $_FILES[$file_field]['error'] != 0) {
            throw new RuntimeException("No file uploaded or an upload error occurred.");
        }

        $file_info = pathinfo($_FILES[$file_field]['name']);
        $name = $file_info['filename'];
        $ext = strtolower($file_info['extension']); // Force lowercase for consistency



        if (!in_array($ext, $allowed_extensions)) {
            throw new UnexpectedValueException("Invalid file extension: " . $ext);
        }


        if (!in_array(strtolower($_FILES[$file_field]["type"]), $allowed_mime_types)) {  // Force lowercase for MIME
             throw new UnexpectedValueException("Invalid file type: " . $_FILES[$file_field]["type"]);
        }

        if ($_FILES[$file_field]["size"] > $max_size) {
            throw new RuntimeException("File is too big");
        }

        if ($check_image) {
            if (!getimagesize($_FILES[$file_field]['tmp_name'])) {
                throw new UnexpectedValueException("Uploaded file is not a valid image");
            }
            // Additional image processing with GD or ImageMagick would go here
            //  Example (GD):
            //  $image = imagecreatefromstring(file_get_contents($_FILES[$file_field]['tmp_name']));
            //  imagejpeg($image, $path . $newname, 90); // Save as JPEG with 90% quality
            //  imagedestroy($image);
        }



        $newname = ($random_name) ? uniqid('', true) . '.' . $ext : $name . '.' . $ext;


        if (file_exists($path.$newname)) {
            throw new RuntimeException("A file with this name already exists");
        }


        if (!move_uploaded_file($_FILES[$file_field]['tmp_name'], $path.$newname)) {
             throw new RuntimeException("Error moving uploaded file");
        }

        return ['filepath' => $path, 'filename' => $newname]; // Return success info

    } catch (Exception $e) {  // Catch any Exception
        return ['error' => $e->getMessage()]; // Return the specific error message
    }
}



if (isset($_POST['submit'])) {
    $file = uploadFile('file', true, true);

    if (isset($file['error'])) {
       $message = '<p>' . $file['error'] . '</p>';
    } else {
       $message = "File uploaded successfully: " . $file['filename'];
    }
    echo $message;
}



?>

<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="post" enctype="multipart/form-data" name="form1" id="form1">
<input name="file" type="file" id="imagee" />
<input name="submit" type="submit" value="Upload" />
</form>