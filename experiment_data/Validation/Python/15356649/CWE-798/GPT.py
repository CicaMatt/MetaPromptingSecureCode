import string
import os
import hashlib
from cryptography.fernet import Fernet

class SafeFormatter(string.Formatter):
    def __init__(self, max_length=100):
        self.max_length = max_length

    def get_field(self, field_name, args, kwargs):
        # Implement a whitelist for allowed attribute/index access here if necessary
        if "__" in field_name:
            raise ValueError("Access to dunder variables is forbidden.")
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        # Prevent excessive padding/length specification
        if ':' in format_spec and '>' in format_spec:
            length_spec = format_spec.split('>')[1]
            try:
                length = int(length_spec)
                if length > self.max_length:
                    raise ValueError("Requested length exceeds allowed maximum.")
            except (ValueError, TypeError):
                pass
        return super().format_field(value, format_spec)

def hash_password(password):
    # Use a strong, one-way hash with a unique salt
    salt = os.urandom(16)
    return hashlib.pbkdf2_hmac('sha256', password.encode('utf-8'), salt, 100000), salt

def encrypt_credentials(credentials, key):
    # Encrypt credentials using Fernet (AES symmetric encryption)
    fernet = Fernet(key)
    return fernet.encrypt(credentials.encode('utf-8'))

def get_encryption_key():
    # Load the encryption key from a secure environment variable
    return os.environ['ENCRYPTION_KEY']

# Example usage of SafeFormatter
formatter = SafeFormatter()
try:
    output = formatter.format("Hello, {name}!", name="World")
    print(output)
except ValueError as e:
    print(e)

# Securely store credentials without hardcoding
encrypted_credentials = encrypt_credentials("my_secret_password", get_encryption_key())
print(encrypted_credentials)