import sqlite3
import os
from cryptography.fernet import Fernet
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.backends import default_backend
import base64

# Constants
DATABASE_FILE = 'credentials.db'
SALT_FILE = 'salt.bin'
MASTER_PASSWORD_ENV_VAR = 'MASTER_PASSWORD'

def generate_key(master_password, salt):
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
        backend=default_backend()
    )
    key = base64.urlsafe_b64encode(kdf.derive(master_password.encode()))
    return key

def encrypt_data(key, data):
    f = Fernet(key)
    return f.encrypt(data.encode())

def decrypt_data(key, encrypted_data):
    f = Fernet(key)
    return f.decrypt(encrypted_data).decode()

def initialize_database():
    if not os.path.exists(DATABASE_FILE):
        conn = sqlite3.connect(DATABASE_FILE)
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE credentials (
                id INTEGER PRIMARY KEY,
                username TEXT,
                password TEXT
            )
        ''')
        conn.commit()
        conn.close()

def store_credentials(username, password, key):
    encrypted_username = encrypt_data(key, username)
    encrypted_password = encrypt_data(key, password)
    
    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()
    cursor.execute('INSERT INTO credentials (username, password) VALUES (?, ?)', 
                   (encrypted_username, encrypted_password))
    conn.commit()
    conn.close()

def retrieve_credentials(key):
    conn = sqlite3.connect(DATABASE_FILE)
    cursor = conn.cursor()
    cursor.execute('SELECT username, password FROM credentials ORDER BY id DESC LIMIT 1')
    row = cursor.fetchone()
    conn.close()
    
    if row:
        encrypted_username, encrypted_password = row
        username = decrypt_data(key, encrypted_username)
        password = decrypt_data(key, encrypted_password)
        return username, password
    return None, None

def main():
    # Check if master password is set in environment variable
    master_password = os.getenv(MASTER_PASSWORD_ENV_VAR)
    if not master_password:
        raise ValueError(f"Please set the {MASTER_PASSWORD_ENV_VAR} environment variable.")
    
    # Generate or load salt
    if not os.path.exists(SALT_FILE):
        salt = os.urandom(16)
        with open(SALT_FILE, 'wb') as f:
            f.write(salt)
    else:
        with open(SALT_FILE, 'rb') as f:
            salt = f.read()
    
    # Generate encryption key
    key = generate_key(master_password, salt)
    
    # Initialize database if not exists
    initialize_database()
    
    # Example usage
    username = "example_user"
    password = "example_password"
    
    # Store credentials
    store_credentials(username, password, key)
    
    # Retrieve credentials
    retrieved_username, retrieved_password = retrieve_credentials(key)
    print(f"Retrieved Username: {retrieved_username}")
    print(f"Retrieved Password: {retrieved_password}")

if __name__ == "__main__":
    main()