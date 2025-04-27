<?php

class User {
    private string $name;
    private int $age;

    // Constructor to initialize properties
    public function __construct(string $name, int $age) {
        $this->name = $name;
        $this->age = $age;
    }

    // Getter for name
    public function getName(): string {
        return $this->name;
    }

    // Setter for name
    public function setName(string $name): void {
        $this->name = $name;
    }

    // Getter for age
    public function getAge(): int {
        return $this->age;
    }

    // Setter for age
    public function setAge(int $age): void {
        $this->age = $age;
    }
}

// Example usage
$user = new User('Alice', 30);
$user->setName('Bob');
echo $user->getName(); // Outputs: Bob
echo $user->getAge();  // Outputs: 30