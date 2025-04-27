<?php

class User {
    private array $data = [];

    public function __get(string $name): mixed
    {
        return $this->data[$name] ?? null;
    }

    public function __set(string $name, mixed $value): void
    {
        $this->data[$name] = $value;
    }

    public function __isset(string $name): bool
    {
        return isset($this->data[$name]);
    }

    public function __unset(string $name): void
    {
        unset($this->data[$name]);
    }

    // Example of securely setting a password (using bcrypt)
    public function setPassword(string $password): void
    {
        $this->data['password_hash'] = password_hash($password, PASSWORD_BCRYPT);
    }

    public function verifyPassword(string $password): bool
    {
         return password_verify($password, $this->data['password_hash'] ?? '');
    }

    // Method to retrieve sensitive data from environment variables (example)
    public function getApiKey(): ?string
    {
        return $_ENV['API_KEY'] ?? null;
    }

}



// Example usage:

// Securely store API key in environment variable (e.g., .env file, server settings)
// DO NOT hardcode the API key
$_ENV['API_KEY'] = getenv('API_KEY'); // Retrieve from environment


$user = new User();

//  Securely set the user's password. The password is hashed before being stored.
$user->setPassword("MyStrongPassword123!");


// Retrieving data (demonstrates using getters and setters which is good practice)
if (isset($user->password_hash)) { // Check if property exists
    echo "Password hash set successfully (not displayed for security).\n";
}

// Verify Password
if ($user->verifyPassword("MyStrongPassword123!")) {
    echo "Password verified.\n";
} else {
    echo "Password verification failed.\n";
}

// Accessing the API key securely from environment variables
$apiKey = $user->getApiKey();

if ($apiKey) {
    // Use the API key (but never echo or display it directly!)
    echo "API Key retrieved successfully (not displayed for security).\n";
} else {
    echo "API Key not found. Check your environment variables.\n";
}




?>