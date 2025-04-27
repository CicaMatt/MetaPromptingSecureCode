import os
import sqlite3
from cryptography.fernet import Fernet
import getpass  # For secure password input during initial setup

# Configuration file path (outside the codebase)
CONFIG_FILE = os.path.expanduser("~/.my_app_config")

def generate_key():
    """Generates a new encryption key."""
    key = Fernet.generate_key()
    return key

def encrypt_password(password, key):
    """Encrypts a password using the provided key."""
    f = Fernet(key)
    encrypted_password = f.encrypt(password.encode())
    return encrypted_password

def decrypt_password(encrypted_password, key):
    """Decrypts an encrypted password using the provided key."""
    f = Fernet(key)
    decrypted_password = f.decrypt(encrypted_password).decode()
    return decrypted_password


def setup_credentials():
    """Guides the user through setting up encrypted credentials."""
    key = generate_key()
    username = input("Enter your username: ")
    password = getpass.getpass("Enter your password: ")  # Secure password input
    encrypted_password = encrypt_password(password, key)

    with open(CONFIG_FILE, "wb") as config_file:
        config_file.write(key + b'\n')  # Store the key in config file
        config_file.write(username.encode() + b'\n')
        config_file.write(encrypted_password + b'\n')

    print(f"Credentials saved securely to {CONFIG_FILE}")


def get_credentials():
    """Retrieves and decrypts credentials from the configuration file."""
    try:
        with open(CONFIG_FILE, "rb") as config_file:
            key = config_file.readline().strip()
            username = config_file.readline().strip().decode()
            encrypted_password = config_file.readline().strip()
            password = decrypt_password(encrypted_password, key)
            return username, password

    except FileNotFoundError:
        print("Configuration file not found. Please run setup.")
        return None, None

if __name__ == "__main__":
    if not os.path.exists(CONFIG_FILE):
        setup_credentials()

    username, password = get_credentials()

    if username and password:
        # Now you can use the username and password securely
        print("Retrieved credentials successfully (not printing for security).")
        # Example of using the credentials:
        # ... Your code to interact with the 3rd party service ...
    else:
      exit(1)