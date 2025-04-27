import sqlite3
from cryptography.fernet import Fernet
import os
import base64
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

# Generate a secure encryption key using PBKDF2HMAC
def generate_key(password: str, salt: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
    )
    return base64.urlsafe_b64encode(kdf.derive(password.encode()))

# Encrypt data using Fernet symmetric encryption
def encrypt_data(data: str, key: bytes) -> bytes:
    fernet = Fernet(key)
    return fernet.encrypt(data.encode())

# Decrypt data using Fernet symmetric encryption
def decrypt_data(encrypted_data: bytes, key: bytes) -> str:
    fernet = Fernet(key)
    return fernet.decrypt(encrypted_data).decode()

# Initialize the SQLite database and create a table for credentials
def initialize_db(db_path: str):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS credentials (
            id INTEGER PRIMARY KEY,
            username TEXT NOT NULL,
            password TEXT NOT NULL
        )
    ''')
    conn.commit()
    conn.close()

# Store credentials securely in the database
def store_credentials(db_path: str, username: str, password: str, key: bytes):
    encrypted_username = encrypt_data(username, key)
    encrypted_password = encrypt_data(password, key)
    
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO credentials (username, password)
        VALUES (?, ?)
    ''', (encrypted_username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve and decrypt credentials from the database
def retrieve_credentials(db_path: str, key: bytes) -> tuple:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute('SELECT username, password FROM credentials ORDER BY id DESC LIMIT 1')
    row = cursor.fetchone()
    conn.close()
    
    if row:
        encrypted_username, encrypted_password = row
        username = decrypt_data(encrypted_username, key)
        password = decrypt_data(encrypted_password, key)
        return username, password
    else:
        raise ValueError("No credentials found in the database.")

# Main function to demonstrate usage
def main():
    # Configuration
    db_path = "credentials.db"
    salt = os.urandom(16)  # Generate a random salt
    master_password = "your_secure_master_password"  # Replace with a strong master password
    
    # Generate encryption key
    key = generate_key(master_password, salt)
    
    # Initialize the database
    initialize_db(db_path)
    
    # Store credentials
    username = "your_username"
    password = "your_password"
    store_credentials(db_path, username, password, key)
    
    # Retrieve and use credentials
    retrieved_username, retrieved_password = retrieve_credentials(db_path, key)
    print(f"Retrieved Username: {retrieved_username}")
    print(f"Retrieved Password: {retrieved_password}")

if __name__ == "__main__":
    main()