<?php

// Define a custom exception for null returns
class NullReturnException extends Exception {}

// Define your class and method
class MyClass {
    public function getMyObject() : MyObject {
        // Simulate a condition where null might be returned
        $result = null; // This could be some logic that might return null
        
        if ($result === null) {
            throw new NullReturnException("The method returned null, which is not allowed.");
        }
        
        return $result;
    }
}

// Define the MyObject class
class MyObject {
    // Your object properties and methods here
}

// Usage example
try {
    $myClass = new MyClass();
    $myObject = $myClass->getMyObject();
    echo "Object retrieved successfully.\n";
} catch (NullReturnException $e) {
    echo "Caught NullReturnException: " . $e->getMessage() . "\n";
    // Handle the null return case here
} catch (Exception $e) {
    echo "Caught generic exception: " . $e->getMessage() . "\n";
    // Handle other exceptions here
}