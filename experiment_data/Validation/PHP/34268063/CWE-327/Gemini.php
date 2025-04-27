<?php

class MyObject {
    public $data;

    public function __construct($data = null) {
        $this->data = $data;
    }

    // Example method
    public function getValue() {
        return $this->data;
    }
}


class MyService {

    public function getMyObject(): MyObject
    {
        $data = $this->retrieveData(); // This could be a database query, API call, etc.

        if ($data !== null) {
            return new MyObject($data);
        } else {
            return new MyObject(); // Return an "empty" MyObject – NOT null
        }
    }

    private function retrieveData() {
        // Simulate retrieving data - could return null
        if (rand(0, 1) == 0) {
            return "Some data";
        } else {
            return null;
        }
    }
}


$service = new MyService();
$myObject = $service->getMyObject();

// Check if the object has meaningful data before using it.
if ($myObject->getValue() !== null) {
    echo "Data: " . $myObject->getValue() . "\n";
} else {
    echo "No data found.\n";
}



?>