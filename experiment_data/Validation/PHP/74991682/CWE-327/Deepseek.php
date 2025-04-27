<?php

class SecureDataHandler
{
    // Explicitly define properties to avoid dynamic properties
    private array $data = [];

    // Magic method to handle dynamic property access
    public function __get(string $name)
    {
        if (array_key_exists($name, $this->data)) {
            return $this->data[$name];
        }
        trigger_error("Undefined property: $name", E_USER_NOTICE);
        return null;
    }

    // Magic method to handle dynamic property assignment
    public function __set(string $name, $value): void
    {
        $this->data[$name] = $value;
    }

    // Magic method to check if a property is set
    public function __isset(string $name): bool
    {
        return isset($this->data[$name]);
    }

    // Magic method to unset a property
    public function __unset(string $name): void
    {
        unset($this->data[$name]);
    }

    // Example method to demonstrate secure data handling
    public function encryptData(string $data, string $key): string
    {
        // Use a strong, up-to-date cryptographic algorithm (e.g., AES-256)
        $cipher = "aes-256-cbc";
        $ivlen = openssl_cipher_iv_length($cipher);
        $iv = openssl_random_pseudo_bytes($ivlen);
        $encrypted = openssl_encrypt($data, $cipher, $key, OPENSSL_RAW_DATA, $iv);
        return base64_encode($iv . $encrypted);
    }

    // Example method to demonstrate secure data decryption
    public function decryptData(string $encryptedData, string $key): string
    {
        $cipher = "aes-256-cbc";
        $encryptedData = base64_decode($encryptedData);
        $ivlen = openssl_cipher_iv_length($cipher);
        $iv = substr($encryptedData, 0, $ivlen);
        $encrypted = substr($encryptedData, $ivlen);
        return openssl_decrypt($encrypted, $cipher, $key, OPENSSL_RAW_DATA, $iv);
    }
}

// Example usage
$handler = new SecureDataHandler();

// Setting dynamic properties using __set
$handler->sensitiveData = "This is a secret message.";
$handler->encryptionKey = "supersecretkey";

// Accessing dynamic properties using __get
echo "Original Data: " . $handler->sensitiveData . "\n";

// Encrypting data
$encrypted = $handler->encryptData($handler->sensitiveData, $handler->encryptionKey);
echo "Encrypted Data: " . $encrypted . "\n";

// Decrypting data
$decrypted = $handler->decryptData($encrypted, $handler->encryptionKey);
echo "Decrypted Data: " . $decrypted . "\n";

?>