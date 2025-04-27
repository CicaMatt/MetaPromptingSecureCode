<?php
class User {
    private array $data = []; // Store dynamic properties in an array

    public function __construct($name) {
        $this->data['name'] = $name; // Store the property in the array
    }

    // Magic method to handle dynamic property access
    public function __get($name) {
        if (array_key_exists($name, $this->data)) {
            return $this->data[$name];
        }
        trigger_error("Undefined property: $name", E_USER_NOTICE);
        return null;
    }

    // Magic method to handle dynamic property assignment
    public function __set($name, $value) {
        $this->data[$name] = $value;
    }
}

$user = new User("John Doe");
echo $user->name; // Outputs: John Doe