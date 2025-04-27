import base64
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
import os
from cryptography.fernet import Fernet
import hashlib
import secrets

def encrypt_message(message, key, iv):
    """Encrypts a message using AES-128-CBC."""
    cipher = AES.new(key.encode(), AES.MODE_CBC, iv.encode())
    padded_message = pad(message.encode(), AES.block_size)
    encrypted_message = cipher.encrypt(padded_message)
    return base64.b64encode(encrypted_message).decode()

def generate_key_from_password(password, salt):
    """Generates a key from a password using PBKDF2."""
    key = hashlib.pbkdf2_hmac('sha256', password.encode(), salt, 100000, dklen=16)  # Use a strong KDF
    return key


def get_credentials():
    """Retrieves credentials from environment variables (more secure than hardcoding)."""
    try:
        password = os.environ["ENCRYPTION_PASSWORD"] 
        salt = os.environ["SALT"]
        if not password or not salt:
           raise ValueError("Password or salt is missing")
        return password, salt.encode() # Salt must be bytes
    except KeyError:
        raise ValueError("Environment variables 'ENCRYPTION_PASSWORD' and 'SALT' must be set.")
    except (TypeError, ValueError) as e: # Catch specific exceptions
        raise ValueError(f"Invalid password or salt format: {e}")


def main():
    """Main function to demonstrate encryption."""
    try:
        password, salt = get_credentials()
        key = generate_key_from_password(password, salt)
        iv = secrets.token_hex(8) # Generate a random IV

        message = "this is a message"
        encrypted_message = encrypt_message(message, key.hex(), iv) # key to hex for string representation

        print(f"IV: {iv}")
        print(f"MSG: {message}")
        print(f"OUT: {encrypted_message}")

        # Example of storing encrypted data with Fernet for future decryption:
        f = Fernet(base64.urlsafe_b64encode(key)) # Use key with Fernet
        token = f.encrypt(message.encode())
        print(f"Fernet Token: {token.decode()}")



    except ValueError as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    main()