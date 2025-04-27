<?php
function uploadFile ($file_field = null, $check_image = false, $random_name = false) {

    //Config Section    
    //Set file upload path
    $path = 'uploads/'; // with trailing slash
    //Set max file size in bytes
    $max_size = 1000000;
    //Set default file extension whitelist
    $whitelist_ext = array('jpeg','jpg','png','gif');
    //Set default file type whitelist
    $whitelist_type = array('image/jpeg', 'image/jpg', 'image/png','image/gif');

    // The Validation
    // Create an array to hold any output
    $out = array('error'=>null);

    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name";           
    }

    if (!$path) {
        $out['error'][] = "Please specify a valid upload path";               
    }

    if (count($out['error'])>0) {
        return $out;
    }

    // Ensure the upload directory is highly restrictive
    if (!is_dir($path) || !is_writable($path)) {
        $out['error'][] = 'Upload directory is not writable or does not exist';
        return $out;
    }

    //Make sure that there is a file
    if((!empty($_FILES[$file_field])) && ($_FILES[$file_field]['error'] == 0)) {

        // Get filename
        $file_info = pathinfo($_FILES[$file_field]['name']);
        $name = $file_info['filename'];
        $ext = $file_info['extension'];

        //Check file has the right extension           
        if (!in_array(strtolower($ext), $whitelist_ext)) {
            $out['error'][] = "Invalid file Extension";
        }

        //Check that the file is of the right type
        $file_mime = mime_content_type($_FILES[$file_field]['tmp_name']);
        if (!in_array($file_mime, $whitelist_type)) {
            $out['error'][] = "Invalid file Type";
        }

        //Check that the file is not too big
        if ($_FILES[$file_field]["size"] > $max_size) {
            $out['error'][] = "File is too big";
        }

        //If $check_image is set as true, check if it's a real image
        if ($check_image) {
            if (!getimagesize($_FILES[$file_field]['tmp_name'])) {
                $out['error'][] = "Uploaded file is not a valid image";
            }
        }

        //Create full filename including path
        if ($random_name) {
            // Generate random filename
            $tmp = md5(uniqid(mt_rand(), true));

            if (!$tmp || $tmp == '') {
                $out['error'][] = "File must have a name";
            }     
            $newname = $tmp.'.'.$ext;                                
        } else {
            $newname = $name.'.'.$ext;
        }

        // Change file name to avoid conflicts
        $newname = time() . '-' . $newname;

        //Check if file already exists on server
        if (file_exists($path.$newname)) {
            $out['error'][] = "A file with this name already exists";
        }

        if (count($out['error'])>0) {
            //The file has not correctly validated
            return $out;
        } 

        if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $path.$newname)) {
            // Optionally process the image further here using GD or Imagick
            $out['filepath'] = $path;
            $out['filename'] = $newname;
            return $out;
        } else {
            $out['error'][] = "Server Error!";
        }

    } else {
        $out['error'][] = "No file uploaded";
        return $out;
    }      
}

// Create .htaccess file to prevent script execution in the upload directory
file_put_contents('uploads/.htaccess', "Options -Indexes\n<FilesMatch \"\.(php|php\.)$\">\nDeny from all\n</FilesMatch>");

if (isset($_POST['submit'])) {
    $file = uploadFile('file', true, true);
    if (is_array($file['error'])) {
        $message = '';
        foreach ($file['error'] as $msg) {
            $message .= '<p>'.$msg.'</p>';    
        }
    } else {
        $message = "File uploaded successfully: ".$file['filename'];
    }
    echo $message;
}
?>

<!-- HTML Form -->
<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']); ?>" method="post" enctype="multipart/form-data" name="form1" id="form1">
    <input name="file" type="file" id="imagee" />
    <input name="submit" type="submit" value="Upload" />
</form>