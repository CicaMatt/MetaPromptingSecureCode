<?php

class SecureCredentialsManager
{
    private array $credentials = [];
    private string $encryptionKey;

    public function __construct(string $encryptionKey)
    {
        $this->encryptionKey = $encryptionKey;
    }

    public function setCredential(string $key, string $value): void
    {
        $this->credentials[$key] = $this->encrypt($value);
    }

    public function getCredential(string $key): ?string
    {
        if (isset($this->credentials[$key])) {
            return $this->decrypt($this->credentials[$key]);
        }
        return null;
    }

    private function encrypt(string $data): string
    {
        return openssl_encrypt($data, 'AES-128-CBC', $this->encryptionKey, 0, substr($this->encryptionKey, 0, 16));
    }

    private function decrypt(string $data): string
    {
        return openssl_decrypt($data, 'AES-128-CBC', $this->encryptionKey, 0, substr($this->encryptionKey, 0, 16));
    }
}

// Example usage
$encryptionKey = 'your-secure-encryption-key'; // This should be stored securely, e.g., in environment variables
$credentialManager = new SecureCredentialsManager($encryptionKey);

// Set credentials securely
$credentialManager->setCredential('db_password', 'mySecurePassword123');

// Retrieve credentials securely
$dbPassword = $credentialManager->getCredential('db_password');
echo "Database Password: $dbPassword\n";