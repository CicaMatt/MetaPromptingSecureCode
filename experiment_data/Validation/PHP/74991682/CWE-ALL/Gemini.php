<?php

class User {
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


    // Example of secure credential handling (relevant to CWE-259, CWE-798)
    public function setPassword(string $password): void {
        // Use password_hash with PASSWORD_DEFAULT for strong hashing.
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

        // Store the hashed password (not the plain text password)
        $this->dynamicProperties['password'] = $hashedPassword; 
    }

    public function verifyPassword(string $password): bool {
         return password_verify($password, $this->dynamicProperties['password'] ?? '');
    }

     // Example illustrating secure certificate handling (relevant to CWE-295, CWE-327) if applicable to the class.
    public function connectToSecureAPI(string $apiUrl, string $certificatePath): bool {
        // Perform thorough certificate validation. Example:
        $contextOptions = [
            'ssl' => [
                'verify_peer' => true,
                'verify_peer_name' => true,
                'cafile' => $certificatePath // Path to trusted CA certificate bundle
            ]
        ];

        $context = stream_context_create($contextOptions);
        $result = file_get_contents($apiUrl, false, $context);

        if ($result === false) {
            // Handle error appropriately, perhaps throw a specific exception 
            throw new \RuntimeException("Failed to connect to API: " . error_get_last()['message']); 
        }
        return true;
    }


}


// Example Usage:
$user = new User();
$user->name = "John Doe";
$user->setPassword("SecureP@sswOrd"); // Store hashed password

echo $user->name . PHP_EOL;  // Accessing dynamic property


if ($user->verifyPassword("SecureP@sswOrd")) {
    echo "Password verified." . PHP_EOL;
} else {
    echo "Incorrect password." . PHP_EOL;
}


// Avoid obsolete functions. Always use up-to-date functions (CWE-477)
// Use specific exception types (CWE-397) as demonstrated above.



// Example secure API connection if applicable (CWE-295, CWE-327)

try {
     $user->connectToSecureAPI('https://secureapi.example.com', '/path/to/ca-bundle.crt');
     echo "Successfully connected to the secure API" . PHP_EOL;
} catch (\RuntimeException $e) {
    echo "Error: " . $e->getMessage() . PHP_EOL; // Specific exception handling
}