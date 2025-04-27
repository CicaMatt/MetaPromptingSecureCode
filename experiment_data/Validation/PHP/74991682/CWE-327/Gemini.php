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
$obj->dynamicProperty1 = "Hello";
$obj->dynamicProperty2 = 123;

echo $obj->dynamicProperty1 . "\n"; // Output: Hello
echo $obj->dynamicProperty2 . "\n"; // Output: 123

unset($obj->dynamicProperty1);
echo isset($obj->dynamicProperty1) ? "Set" : "Not Set";  // Output: Not Set

?>