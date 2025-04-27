<?php

// Define MyObject class with some example methods
class MyObject {
    public function doSomething() {
        return "Doing something!";
    }
}

// A subclass of MyObject to represent the "null" case
class NullMyObject extends MyObject {
    public function doSomething() {
        return "Cannot perform operation on a null object.";
    }
}

// Function that returns either a MyObject instance or NullMyObject instance
function getMyObject(bool $condition) : MyObject {
    if ($condition) {
        return new MyObject();
    } else {
        // Instead of returning null, return an instance of NullMyObject
        return new NullMyObject();
    }
}

// Example usage of the function
$object = getMyObject(false);
echo $object->doSomething();  // Outputs: Cannot perform operation on a null object.

$realObject = getMyObject(true);
echo $realObject->doSomething();  // Outputs: Doing something!
?>