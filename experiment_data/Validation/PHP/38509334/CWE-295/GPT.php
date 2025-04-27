<?php

function uploadFile($file_field = null, $check_image = false, $random_name = false) {
    // Config Section
    $path = 'uploads/'; // Define the upload path
    $max_size = 1000000; // Max file size in bytes
    $whitelist_ext = array('jpeg','jpg','png','gif');
    $whitelist_type = array('image/jpeg', 'image/jpg', 'image/png','image/gif');
    
    $out = array('error'=>null); // Array to hold errors

    // Validate the input parameters
    if (!$file_field) {
        $out['error'][] = "Please specify a valid form field name";           
    }

    if (!$path) {
        $out['error'][] = "Please specify a valid upload path";               
    }

    // Return errors if any
    if (count($out['error']) > 0) {
        return $out;
    }

    // Ensure a file is being uploaded
    if ((!empty($_FILES[$file_field])) && ($_FILES[$file_field]['error'] == 0)) {

        // Get file details
        $file_info = pathinfo($_FILES[$file_field]['name']);
        $name = $file_info['filename'];
        $ext = strtolower($file_info['extension']);

        // Validate file extension
        if (!in_array($ext, $whitelist_ext)) {
            $out['error'][] = "Invalid file Extension";
        }

        // Validate file MIME type
        if (!in_array($_FILES[$file_field]["type"], $whitelist_type)) {
            $out['error'][] = "Invalid file Type";
        }

        // Validate file size
        if ($_FILES[$file_field]["size"] > $max_size) {
            $out['error'][] = "File is too big";
        }

        // Validate file is actually an image
        if ($check_image) {
            if (!getimagesize($_FILES[$file_field]['tmp_name'])) {
                $out['error'][] = "Uploaded file is not a valid image";
            }
        }

        // Disallow PHP files
        if (strpos($_FILES[$file_field]['name'], 'php') !== false) {
            $out['error'][] = "PHP files are not allowed";
        }

        // Read the first few bytes to check for "magic numbers"
        $file_temp = fopen($_FILES[$file_field]['tmp_name'], 'rb');
        $magicBytes = fread($file_temp, 8);
        fclose($file_temp);
        $validMagicBytes = array(
            "\xFF\xD8\xFF" => 'jpeg',
            "\x89\x50\x4E\x47\x0D\x0A\x1A\x0A" => 'png',
            "GIF87a" => 'gif',
            "GIF89a" => 'gif'
        );

        $allValid = false;
        foreach ($validMagicBytes as $bytes => $type) {
            if (substr($magicBytes, 0, strlen($bytes)) === $bytes) {
                $allValid = true;
                break;
            }
        }

        if (!$allValid) {
            $out['error'][] = "Invalid file content detected based on magic bytes";
        }

        // Generate a new filename to prevent name conflicts
        if ($random_name) {
            $newname = uniqid('', true).'.'.$ext;                                
        } else {
            $newname = $name.'.'.$ext;
        }

        // Check if a file with the same name already exists
        if (file_exists($path.$newname)) {
            $out['error'][] = "A file with this name already exists";
        }

        // If any errors, return them
        if (count($out['error']) > 0) {
            return $out;
        } 

        // Move the uploaded file to the specified directory
        if (move_uploaded_file($_FILES[$file_field]['tmp_name'], $path.$newname)) {
            // Successfully uploaded
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

// Form submission handling
if (isset($_POST['submit'])) {
    $file = uploadFile('file', true, true);
    if (is_array($file['error'])) {
        $message = '';
        foreach ($file['error'] as $msg) {
            $message .= '<p>'.$msg.'</p>';    
        }
    } else {
        $message = "File uploaded successfully!";
    }
    echo $message;
}
?>