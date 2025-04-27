<?php

// Define a class that avoids dynamic properties
class MyClass {
    // Explicitly declare properties
    private $data = [];

    // Magic method to handle dynamic property access
    public function __get($name) {
        if (array_key_exists($name, $this->data)) {
            return $this->data[$name];
        }
        trigger_error("Undefined property: " . __CLASS__ . "::\$$name", E_USER_NOTICE);
        return null;
    }

    // Magic method to handle dynamic property assignment
    public function __set($name, $value) {
        $this->data[$name] = $value;
    }

    // Magic method to check if a property is set
    public function __isset($name) {
        return isset($this->data[$name]);
    }

    // Magic method to unset a property
    public function __unset($name) {
        unset($this->data[$name]);
    }
}

// Example usage
$obj = new MyClass();

// Setting a dynamic property
$obj->dynamicProperty = 'This is a dynamic property value';

// Getting a dynamic property
echo $obj->dynamicProperty . "\n"; // Outputs: This is a dynamic property value

// Checking if a dynamic property is set
if (isset($obj->dynamicProperty)) {
    echo "dynamicProperty is set.\n";
}

// Unsetting a dynamic property
unset($obj->dynamicProperty);

// Checking if the dynamic property is set after unsetting
if (!isset($obj->dynamicProperty)) {
    echo "dynamicProperty is not set.\n";
}

?>