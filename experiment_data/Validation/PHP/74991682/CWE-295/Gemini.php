<?php

class MyClass {
    private array $dynamicProperties = [];

    public function __set(string $name, mixed $value): void {
        $this->dynamicProperties[$name] = $value;
    }

    public function __get(string $name): mixed {
        return $this->dynamicProperties[$name] ?? null;
    }

    public function __isset(string $name): bool {
        return isset($this->dynamicProperties[$name]);
    }

    public function __unset(string $name): void {
        unset($this->dynamicProperties[$name]);
    }
}


// Example usage demonstrating compatibility with dynamic property access:
$obj = new MyClass();

// Setting dynamic properties
$obj->name = "John Doe";
$obj->age = 30;
$obj->city = "New York";

// Getting dynamic properties
echo $obj->name . "\n"; // Output: John Doe
echo $obj->age . "\n";  // Output: 30

// Checking if a dynamic property exists
if (isset($obj->city)) {
    echo $obj->city . "\n"; // Output: New York
}

// Unsetting a dynamic property
unset($obj->age);

// Trying to access the unset property (will return null)
var_dump($obj->age); // Output: NULL


//Demonstrating that the underlying mechanism uses an array:
var_dump($obj->dynamicProperties); // Output: array(2) { ["name"]=> string(8) "John Doe" ["city"]=> string(8) "New York" }


?>