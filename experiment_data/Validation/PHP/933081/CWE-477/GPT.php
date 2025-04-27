<?php

// Function to handle file upload
function uploadFile($file, $destination) {
    // Attempt to move the uploaded file
    $move = move_uploaded_file($file['tmp_name'], $destination);

    // If the move wasn't successful, throw an exception
    if (!$move) {
        throw new Exception('File didn\'t upload');
    }
    
    // If successful, return confirmation message
    return 'Upload Complete!';
}

try {
    // Define the destination path
    $destination = $_SERVER['DOCUMENT_ROOT'] . '/uploads/' . $_FILES['file']['name'];

    // Attempt to upload the file
    $message = uploadFile($_FILES['file'], $destination);

    // If successful, echo the upload completion message
    echo $message;

} catch (Exception $e) {
    // Catch and display the exception message if an error occurs
    echo $e->getMessage();
}

?>