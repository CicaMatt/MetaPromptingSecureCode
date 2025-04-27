<?php

class MyObject {
    public $data;

    public function __construct($data = null) {
        $this->data = $data;
    }
}

class MyObjectManager {

    /**
     * Retrieves a MyObject or throws an exception if retrieval fails.
     *
     * @param int $id The ID of the object to retrieve.
     * @return MyObject|null
     * @throws \InvalidArgumentException If the ID is invalid.
     * @throws \RuntimeException If the object cannot be found.
     */
    public function getMyObject(int $id): ?MyObject {
        if ($id < 0) {
            throw new \InvalidArgumentException("Invalid ID: $id");
        }

        // Simulate database retrieval or other operation that might fail
        if ($id % 2 === 0) {  // Even IDs "exist"
            return new MyObject("Data for ID: $id");
        } else {
            throw new \RuntimeException("Object not found for ID: $id"); 
        }
        
    }
}


$manager = new MyObjectManager();

try {
    $myObject = $manager->getMyObject(2); // This will succeed

    if ($myObject !== null) { // Check if the object is null
        echo "Object data: " . $myObject->data . PHP_EOL; 
    } else {
        echo "Object is null." . PHP_EOL;
    }

    $myObject = $manager->getMyObject(-1); // This will throw InvalidArgumentException

} catch (\InvalidArgumentException $e) {
    echo "Invalid argument error: " . $e->getMessage() . PHP_EOL;
    // Handle invalid argument error, e.g., log, display error message
} catch (\RuntimeException $e) {
    echo "Runtime error: " . $e->getMessage() . PHP_EOL;
    // Handle runtime error, e.g., log, retry, alternative action
} 



try {
    $myObject = $manager->getMyObject(3); // This will throw RuntimeException
    if ($myObject !== null) {
        echo $myObject->data . PHP_EOL; 
    } else {
         echo "Object is null." . PHP_EOL;
    }

} catch (\InvalidArgumentException $e) {
    echo "Invalid argument error: " . $e->getMessage() . PHP_EOL;
    // Handle invalid argument error
} catch (\RuntimeException $e) {
    echo "Runtime error: " . $e->getMessage() . PHP_EOL;
    // Handle runtime error
}


?>