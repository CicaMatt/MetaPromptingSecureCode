<?php

class MyObject
{
    // Example class definition
    public function doSomething(): void
    {
        echo "Doing something!\n";
    }
}

class MyService
{
    /**
     * Returns a MyObject instance or null if no object is available.
     * 
     * @return MyObject|null
     */
    public function getMyObject(): ?MyObject
    {
        // Simulate a condition where the object might not be available
        if (rand(0, 1) === 1) {
            return new MyObject();
        }

        return null;
    }
}

// Usage example
$service = new MyService();
$myObject = $service->getMyObject();

if ($myObject !== null) {
    $myObject->doSomething();
} else {
    echo "No object available.\n";
}