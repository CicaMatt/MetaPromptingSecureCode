<?php

class MyObject {
    public $data;

    public function __construct($data = null) {
        $this->data = $data;
    }
}

class MyService {

    /**
     * Retrieves a MyObject instance or null if not found.
     *
     * @return MyObject|null
     */
    public function getMyObject(): ?MyObject {
        // Simulate database query or API call that might return null
        $data = $this->fetchDataFromSource(); 

        if ($data) {
            return new MyObject($data);
        } else {
            return null; // Explicitly return null when no object is found
        }
    }


    private function fetchDataFromSource() {
       // Replace with actual data retrieval logic (e.g., database query)
       // This example randomly returns data or null:
       return rand(0, 1) ? ['id' => 1, 'name' => 'Test Object'] : null;
    }
}



// Example usage:

$service = new MyService();
$object = $service->getMyObject();

if ($object !== null) {  // Check for null before accessing properties
    echo "Object data: " . json_encode($object->data) . "\n";
} else {
    echo "No object found.\n";
}


// Example of handling potential null in a chain:

$dataValue = $service->getMyObject()?->data['name'] ?? "Default Value"; 
echo "Data Value (with null coalescing): " . $dataValue . "\n";



?>