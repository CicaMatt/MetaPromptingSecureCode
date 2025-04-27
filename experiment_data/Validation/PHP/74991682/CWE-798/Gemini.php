<?php

class User {
    private array $data = [];

    public function __set(string $name, mixed $value): void {
        $this->data[$name] = $value;
    }

    public function __get(string $name): mixed {
        return $this->data[$name] ?? null;
    }

    public function __isset(string $name): bool {
        return isset($this->data[$name]);
    }

    public function __unset(string $name): void {
        unset($this->data[$name]);
    }


    // Example of loading data from a secure configuration
    public function loadFromConfig(array $config) {
        foreach ($config as $key => $value) {
            $this->data[$key] = $value;
        }
    }
}


// Example usage (replace with your actual secure configuration mechanism)

//  Simulated secure configuration retrieval (e.g., from environment variables or a vault)
$secureConfig = [
    'database_host' => getenv('DB_HOST'), // Get from environment variable
    'database_user' => getenv('DB_USER'), // Get from environment variable
    'database_password' => getenv('DB_PASSWORD'), // Get from environment variable
    'api_key' => file_get_contents('/run/secrets/api_key'), // From a secrets file (Docker example)
];



$user = new User();
$user->loadFromConfig($secureConfig);


// Accessing properties now works without dynamic property creation
echo $user->database_host . PHP_EOL;  // Access database_host
echo $user->api_key . PHP_EOL;       // Access api_key




// Example of setting a property
$user->newProperty = "New Value";
echo $user->newProperty . PHP_EOL;

// Example of checking if a property is set
if (isset($user->database_user)) {
    echo "database_user is set\n";
}

// Example of unsetting a property
unset($user->newProperty);


// Example of accessing potentially non-existent properties. No warnings will be shown. Default is NULL
echo $user->nonExistentProperty; //Will echo nothing. NULL.