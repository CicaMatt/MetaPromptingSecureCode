<?php

/**
 * A generic NullObject class that can be used as a placeholder for any type.
 */
class NullObject
{
    // You can add methods or properties here if needed, but by default, it's empty.
}

/**
 * A sample class representing a domain object.
 */
class MyObject
{
    public function doSomething(): string
    {
        return "Doing something!";
    }
}

/**
 * A service class that returns an object or a NullObject if no object is available.
 */
class MyService
{
    /**
     * Returns a MyObject or a NullObject if no object is available.
     *
     * @return MyObject|NullObject
     */
    public function getMyObject(): MyObject
    {
        // Simulate a condition where no object is available.
        $objectAvailable = false;

        if ($objectAvailable) {
            return new MyObject();
        }

        // Return a NullObject instead of null.
        return new NullObject();
    }
}

// Usage example
$service = new MyService();
$result = $service->getMyObject();

if ($result instanceof NullObject) {
    // Handle the case where no object is available.
    echo "No object available. Using NullObject as a placeholder.\n";
} else {
    // Proceed with the actual object.
    echo $result->doSomething() . "\n";
}

?>