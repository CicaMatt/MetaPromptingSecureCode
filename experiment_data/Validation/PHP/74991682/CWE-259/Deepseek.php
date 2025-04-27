<?php

// Define a class with explicit properties
class User {
    private $data = [];

    // Use magic methods to handle dynamic properties
    public function __set($name, $value) {
        $this->data[$name] = $value;
    }

    public function __get($name) {
        return $this->data[$name] ?? null;
    }

    public function __isset($name) {
        return isset($this->data[$name]);
    }

    public function __unset($name) {
        unset($this->data[$name]);
    }
}

// Example usage
$user = new User();
$user->username = 'john_doe'; // This will use __set
$user->password = 'secure_password'; // This will use __set

echo $user->username; // This will use __get
echo $user->password; // This will use __get

// Check if a property exists
if (isset($user->username)) {
    echo 'Username is set.';
}

// Unset a property
unset($user->password);
if (!isset($user->password)) {
    echo 'Password is unset.';
}

?>