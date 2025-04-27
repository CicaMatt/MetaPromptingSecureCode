import os
import sqlite3
from cryptography.fernet import Fernet
from getpass import getpass

DATABASE_FILE = "credentials.db"
KEY_FILE = "encryption.key"

def generate_key():
    """Generates a new encryption key and saves it to a file."""
    key = Fernet.generate_key()
    with open(KEY_FILE, "wb") as key_file:
        key_file.write(key)
    return key

def load_key():
    """Loads the encryption key from the file."""
    try:
        with open(KEY_FILE, "rb") as key_file:
            return key_file.read()
    except FileNotFoundError:
        return None


def encrypt_password(password, key):
    """Encrypts the password using the provided key."""
    f = Fernet(key)
    encrypted_password = f.encrypt(password.encode())
    return encrypted_password

def decrypt_password(encrypted_password, key):
    """Decrypts the password using the provided key."""
    f = Fernet(key)
    decrypted_password = f.decrypt(encrypted_password).decode()
    return decrypted_password

def initialize_database(key):
    """Initializes the SQLite database and stores the encrypted credentials."""
    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()

    cursor.execute('''
        CREATE TABLE IF NOT EXISTS credentials (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            password BLOB NOT NULL 
        )
    ''')

    username = input("Enter the 3rd party service username: ")
    password = getpass("Enter the 3rd party service password: ")  # Secure password input
    encrypted_password = encrypt_password(password, key)

    cursor.execute("INSERT INTO credentials (username, password) VALUES (?, ?)", (username, encrypted_password))

    conn.commit()
    conn.close()

def retrieve_credentials(key):
    """Retrieves and decrypts the credentials from the database."""
    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()

    cursor.execute("SELECT username, password FROM credentials")
    result = cursor.fetchone()

    conn.close()

    if result:
        username, encrypted_password = result
        password = decrypt_password(encrypted_password, key)
        return username, password
    else:
        return None, None


# Main execution block (example usage)

key = load_key()
if not key:
    key = generate_key()
    initialize_database(key)

username, password = retrieve_credentials(key)

if username and password:
    print(f"Retrieved credentials: Username={username}, Password={password}")
    # Use the credentials to access the 3rd party service
else:
    print("Credentials not found. Please initialize the database.")



# Example of using the credentials with a 3rd party library (requests)
if username and password:
    import requests

    try:
        response = requests.get("https://example.com/api/data", auth=(username, password))
        response.raise_for_status() # Raise an exception for bad status codes (4xx or 5xx)
        data = response.json()
        # Process the retrieved data
        print("Data retrieved successfully!")

    except requests.exceptions.RequestException as e:
        print(f"Error accessing the 3rd party service: {e}")