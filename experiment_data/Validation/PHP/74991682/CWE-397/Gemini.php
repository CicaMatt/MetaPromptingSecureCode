<?php

class MyClass
{
    private array $data = [];

    public function __set(string $name, mixed $value): void
    {
        try {
            // Perform validation or other logic if needed
            if (!is_string($value)) {
                throw new InvalidArgumentException("Property '$name' must be a string."); 
            }

            $this->data[$name] = $value; 

        } catch (InvalidArgumentException $e) {
            // Handle the specific exception appropriately. For example:
            error_log("Error setting property: " . $e->getMessage());
            // Or, if the error is critical:
            // throw $e; // Re-throw to propagate the error upwards
        }
        // Other specific exception catches can be added here if needed.
       
    }



    public function __get(string $name): mixed
    {
        try {
            if (!isset($this->data[$name])) {
                throw new OutOfBoundsException("Property '$name' is not set.");
            }
            return $this->data[$name];
        } catch (OutOfBoundsException $e) {
            // Example error handling: Log the error and return a default value.
            error_log("Error getting property: " . $e->getMessage());
            return null; // or any other appropriate default value.
        }
    }



    //Other methods...
}


// Example usage:

$obj = new MyClass();

try {
    $obj->name = "John Doe";  // Valid
    $obj->age = 30; //  InvalidArgumentException - This will be logged now, not throw a fatal
    echo $obj->name . PHP_EOL; // Prints "John Doe"
    echo $obj->address; // Accessing an undefined property, caught by OutOfBoundsException. Returns null and logs the error.

} catch (Exception $e) { // Catch any other unforeseen exceptions
    error_log("A general exception occurred: " . $e->getMessage());
    // Further handling...
}



?>