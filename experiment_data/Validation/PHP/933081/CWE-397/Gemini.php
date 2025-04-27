<?php

try {
    if (!move_uploaded_file($_FILES['file']['tmp_name'], $_SERVER['DOCUMENT_ROOT'].'/uploads/'.$_FILES['file']['name'])) {
        // Throw a specific exception.  Since PHP's built-in exception handling for file uploads is limited, we create a custom one for clarity
        throw new Exception('File upload failed.'); // Could be a more specialized exception like UploadException
    }

    echo 'Upload Complete!';


} catch (Exception $e) {  // Catch the specific exception(s)
    // Log the error for debugging.
    error_log("File upload error: " . $e->getMessage()); 

    //  Provide a user-friendly error message. Don't expose sensitive details in production.
    die ('File didn\'t upload. Please try again later.'); 

    // Alternative: You could retry the upload, or perform other error handling actions.
    // If the error can't be handled here, re-throw it:
    // throw $e;
} 


//  The following code demonstrates creating a custom exception. While not strictly
// necessary for this simple example, it's best practice for larger applications:

class UploadException extends Exception {} //  Inherit from the base Exception class


// Example usage of the custom exception:

/*
try {
    // .... upload logic ...
    if (--upload fails--) {
       throw new UploadException("Error uploading file: Reason X");
    }
} catch (UploadException $e) {  // Catch the specific upload exception
     // Handle UploadException specifically
} catch (Exception $e) { // Catch other general exceptions
     // Generic exception handling
}
*/

?>