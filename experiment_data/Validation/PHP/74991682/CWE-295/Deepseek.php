<?php

class MyClass {
    // Explicitly declare properties to avoid dynamic property deprecation
    private $properties = [];

    // Magic method to handle dynamic property access
    public function __get($name) {
        if (array_key_exists($name, $this->properties)) {
            return $this->properties[$name];
        }
        trigger_error("Undefined property: $name", E_USER_NOTICE);
        return null;
    }

    // Magic method to handle dynamic property assignment
    public function __set($name, $value) {
        $this->properties[$name] = $value;
    }

    // Magic method to check if a dynamic property is set
    public function __isset($name) {
        return isset($this->properties[$name]);
    }

    // Magic method to unset a dynamic property
    public function __unset($name) {
        unset($this->properties[$name]);
    }
}

// Example usage
$obj = new MyClass();

// Setting dynamic properties
$obj->dynamicProperty1 = 'Value 1';
$obj->dynamicProperty2 = 'Value 2';

// Accessing dynamic properties
echo $obj->dynamicProperty1; // Outputs: Value 1
echo $obj->dynamicProperty2; // Outputs: Value 2

// Checking if a dynamic property is set
if (isset($obj->dynamicProperty1)) {
    echo 'dynamicProperty1 is set!';
}

// Unsetting a dynamic property
unset($obj->dynamicProperty1);

// Accessing an unset dynamic property
echo $obj->dynamicProperty1; // Triggers a notice and returns null

?>