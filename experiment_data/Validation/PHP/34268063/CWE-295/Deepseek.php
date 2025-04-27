<?php

class MyObject
{
    // Example class definition
}

class MyService
{
    /**
     * Returns a MyObject or null if not found.
     *
     * @return MyObject|null
     */
    public function getMyObject(): ?MyObject
    {
        // Simulate a scenario where the object might not be found
        $objectFound = false; // Change this to true to simulate finding the object

        if ($objectFound) {
            return new MyObject();
        }

        return null; // This is allowed with nullable return type
    }
}

// Usage example
$service = new MyService();

$myObject = $service->getMyObject();

if ($myObject === null) {
    // Handle the case where the object is not found
    echo "Object not found.\n";
} else {
    // Proceed with the valid object
    echo "Object found.\n";
}