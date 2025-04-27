<?php
class User {
    // Initially, dynamic properties would be assigned like this:
    // public $name;
}

// Using dynamic properties like this:
// $user = new User();
// $user->email = 'user@example.com'; // Deprecated from PHP 8.2
class User {
    // Define private properties
    private $properties = [];

    // Magic setter method
    public function __set($name, $value) {
        $this->properties[$name] = $value;
    }

    // Magic getter method
    public function __get($name) {
        if (array_key_exists($name, $this->properties)) {
            return $this->properties[$name];
        }

        trigger_error("Undefined property: " . __CLASS__ . "::$name", E_USER_NOTICE);
        return null;
    }

    // Optional: to check if a property is set
    public function __isset($name) {
        return isset($this->properties[$name]);
    }

    // Optional: to unset a property
    public function __unset($name) {
        unset($this->properties[$name]);
    }
}

// Example usage
$user = new User();
$user->email = 'user@example.com'; // This will now set the 'email' in the internal array
echo $user->email; // This will retrieve 'user@example.com' from the internal array