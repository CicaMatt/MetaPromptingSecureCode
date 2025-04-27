import os
import sqlite3
from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from getpass import getpass  # For secure password input during setup

DATABASE_FILE = "credentials.db"
SALT_FILE = "salt.bin"
KEY_FILE = "key.bin"


def derive_key(password, salt):
    """Derives a cryptographic key from the password and salt."""
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
    )
    return kdf.derive(password.encode())


def encrypt_data(data, key):
    """Encrypts data using Fernet."""
    f = Fernet(key)
    return f.encrypt(data.encode())


def decrypt_data(data, key):
    """Decrypts data using Fernet."""
    f = Fernet(key)
    return f.decrypt(data).decode()


def initialize_database():
    """Initializes the database and sets up encryption."""
    # Generate salt
    salt = os.urandom(16)
    with open(SALT_FILE, "wb") as f:
        f.write(salt)

    # Get master password from the user (only done once)
    password = getpass("Enter a master password: ")
    key = derive_key(password, salt)
    with open(KEY_FILE, "wb") as f:
        f.write(key)
    
    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()
    cursor.execute(
        """
        CREATE TABLE IF NOT EXISTS credentials (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            service_name TEXT UNIQUE NOT NULL,
            username TEXT NOT NULL,
            password TEXT NOT NULL
        )
        """
    )
    conn.commit()
    conn.close()


def store_credentials(service_name, username, password):
    """Stores encrypted credentials in the database."""
    with open(SALT_FILE, "rb") as f:
        salt = f.read()
    with open(KEY_FILE, "rb") as f:
        key = f.read()


    encrypted_username = encrypt_data(username, key)
    encrypted_password = encrypt_data(password, key)


    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()

    try:
        cursor.execute(
            "INSERT INTO credentials (service_name, username, password) VALUES (?, ?, ?)",
            (service_name, encrypted_username, encrypted_password),
        )
    except sqlite3.IntegrityError:
        cursor.execute(
            "UPDATE credentials SET username = ?, password = ? WHERE service_name = ?",
            (encrypted_username, encrypted_password, service_name),
        )
    
    conn.commit()
    conn.close()


def retrieve_credentials(service_name):
    """Retrieves and decrypts credentials from the database."""
    with open(SALT_FILE, "rb") as f:
        salt = f.read()
    with open(KEY_FILE, "rb") as f:
        key = f.read()
    

    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()
    cursor.execute(
        "SELECT username, password FROM credentials WHERE service_name = ?", (service_name,)
    )
    result = cursor.fetchone()
    conn.close()

    if result:
        encrypted_username, encrypted_password = result
        username = decrypt_data(encrypted_username, key)
        password = decrypt_data(encrypted_password, key)
        return username, password
    else:
        return None


# Example Usage (First time setup):
if not os.path.exists(DATABASE_FILE):
    initialize_database()


# Example usage: Storing credentials
store_credentials("third_party_service", "my_username", "my_secret_password")


# Example usage: Retrieving credentials
username, password = retrieve_credentials("third_party_service")

if username and password:
    # Use the retrieved credentials to interact with the third-party service.
    print(f"Username: {username}")
    print(f"Password: {password}")