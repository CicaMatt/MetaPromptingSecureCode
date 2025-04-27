import sqlite3
from cryptography.fernet import Fernet
import os

# Generate a key for encryption and save it to a file
def generate_key(key_file):
    if not os.path.exists(key_file):
        key = Fernet.generate_key()
        with open(key_file, 'wb') as key_file:
            key_file.write(key)

# Load the encryption key from the file
def load_key(key_file):
    with open(key_file, 'rb') as key_file:
        return key_file.read()

# Encrypt the credentials
def encrypt_credentials(key, username, password):
    cipher_suite = Fernet(key)
    encrypted_username = cipher_suite.encrypt(username.encode())
    encrypted_password = cipher_suite.encrypt(password.encode())
    return encrypted_username, encrypted_password

# Decrypt the credentials
def decrypt_credentials(key, encrypted_username, encrypted_password):
    cipher_suite = Fernet(key)
    username = cipher_suite.decrypt(encrypted_username).decode()
    password = cipher_suite.decrypt(encrypted_password).decode()
    return username, password

# Store the encrypted credentials in the SQLite database
def store_credentials(db_file, key_file, username, password):
    key = load_key(key_file)
    encrypted_username, encrypted_password = encrypt_credentials(key, username, password)

    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS credentials
                      (id INTEGER PRIMARY KEY, username BLOB, password BLOB)''')
    cursor.execute('INSERT INTO credentials (username, password) VALUES (?, ?)',
                   (encrypted_username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve and decrypt the credentials from the SQLite database
def retrieve_credentials(db_file, key_file):
    key = load_key(key_file)

    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute('SELECT username, password FROM credentials ORDER BY id DESC LIMIT 1')
    row = cursor.fetchone()
    conn.close()

    if row:
        encrypted_username, encrypted_password = row
        return decrypt_credentials(key, encrypted_username, encrypted_password)
    else:
        raise ValueError("No credentials found in the database.")

# Example usage
if __name__ == "__main__":
    key_file = 'encryption_key.key'
    db_file = 'credentials.db'
    username = 'my_username'
    password = 'my_password'

    # Generate the encryption key if it doesn't exist
    generate_key(key_file)

    # Store the credentials
    store_credentials(db_file, key_file, username, password)

    # Retrieve the credentials
    retrieved_username, retrieved_password = retrieve_credentials(db_file, key_file)
    print(f"Retrieved Username: {retrieved_username}")
    print(f"Retrieved Password: {retrieved_password}")