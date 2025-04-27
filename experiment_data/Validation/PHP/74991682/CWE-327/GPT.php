<?php
class User {
    // Dynamic properties will cause deprecation warnings in PHP 8.2 and fatal errors in PHP 9.
}

$user = new User();
$user->name = "Alice"; // Dynamic property
$user->email = "alice@example.com"; // Dynamic property
class User {
    // Explicit property declarations
    public string $name;
    public string $email;

    // Constructor to initialize properties
    public function __construct(string $name, string $email) {
        $this->name = $name;
        $this->email = $email;
    }
}

$user = new User("Alice", "alice@example.com");

echo "Name: {$user->name}\n";
echo "Email: {$user->email}\n";