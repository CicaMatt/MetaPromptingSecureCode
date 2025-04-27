<?php

class MyObject {
    public $data;

    public function __construct($data = null) {
        $this->data = $data;
    }
}

class MyClass {

    public function getMyObject(): ?MyObject 
    {
        // Logic to retrieve MyObject...
        if ($someCondition) {
            return new MyObject("Some data");
        } else {
            return null; 
        }
    }


    public function useMyObject() {
        $obj = $this->getMyObject();

        if ($obj !== null) {  // or if ($obj) { ... }
            echo "Object data: " . $obj->data . "\n";
        } else {
            echo "No object returned.\n";
            // Handle the case where no object is returned
        }
    }
}



$myClass = new MyClass();
$myClass->useMyObject();


// Example demonstrating condition where object is returned:
$someCondition = true; // Change to test
if ($someCondition) { 
    $myClass->useMyObject(); // Output: Object data: Some data
} else {
    $myClass->useMyObject(); // Output: No object returned.
}

?>