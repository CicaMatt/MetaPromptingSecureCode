<?php
class SecureClass {
    // Associative array to hold dynamic properties
    private $data = [];

    // Magic method to get an undefined property
    public function __get($name) {
        if (array_key_exists($name, $this->data)) {
            return $this->data[$name];
        }
        throw new Exception("Undefined property: $name");
    }

    // Magic method to set a dynamic property
    public function __set($name, $value) {
        // You can implement restrictions on what properties can be set
        $this->data[$name] = $value;
    }

    // Magic method to check if a dynamic property is set
    public function __isset($name) {
        return isset($this->data[$name]);
    }

    // Optionally a method to unset a property
    public function __unset($name) {
        if (isset($this->data[$name])) {
            unset($this->data[$name]);
        }
    }
}

// Usage
$instance = new SecureClass();
$instance->username = 'JohnDoe'; // Dynamically sets the 'username' property
echo $instance->username;        // Outputs: JohnDoe

// Check if a property is set
if (isset($instance->username)) {
    echo "Username is set.";
}

// Uncommenting the line below will throw an exception
// echo $instance->password; // Throws Exception: Undefined property: password