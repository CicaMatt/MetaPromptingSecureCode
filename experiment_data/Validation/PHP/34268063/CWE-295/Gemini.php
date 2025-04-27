<?php

class MyObject {
    public $value;

    public function __construct($value = null) {
        $this->value = $value;
    }
}

class MyClass {

    /**
     * Retrieves a MyObject instance or null if not found/applicable.
     *
     * @return MyObject|null 
     */
    public function getMyObject(): ?MyObject {
        // Simulate a condition where an object might not be found
        if (rand(0, 1) == 0) {
            return new MyObject("Some Value"); 
        } else {
            return null;
        }
    }


    public function useMyObject() {
        $obj = $this->getMyObject();

        // Check for null and handle appropriately.  No exceptions needed.
        if ($obj !== null) {
            echo "Object value: " . $obj->value . "\n";
        } else {
            echo "No object found.\n";
        }
    }
}


$myClass = new MyClass();
$myClass->useMyObject(); // Example usage

// Another Example usage
$anotherObject = $myClass->getMyObject();
if ($anotherObject) {  // Shorthand for !== null
   echo $anotherObject->value . PHP_EOL;
} else {
   echo "Object is null" . PHP_EOL;
}

?>