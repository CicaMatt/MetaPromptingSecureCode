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


// Example usage:
$obj = new MyClass();
$obj->dynamicProperty1 = "Hello"; // No longer a dynamic property, uses __set
$obj->dynamicProperty2 = 123;      // No longer a dynamic property, uses __set

echo $obj->dynamicProperty1 . PHP_EOL; // Uses __get
echo $obj->dynamicProperty2 . PHP_EOL; // Uses __get

if (isset($obj->dynamicProperty1)) {  // Uses __isset
    echo "dynamicProperty1 is set\n";
}

unset($obj->dynamicProperty1);       // Uses __unset

if (!isset($obj->dynamicProperty1)) { // Uses __isset
    echo "dynamicProperty1 is not set\n";
}

var_dump($obj); // Shows the dynamic properties are stored in $dynamicProperties