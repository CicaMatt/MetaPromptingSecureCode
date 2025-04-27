from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.backends import default_backend
import os
import sqlite3
import base64

# Generate a new random key
def generate_key():
    return os.urandom(32)  # 256-bit key for AES

# Encrypt data using AES-GCM
def encrypt_data(key, data):
    aes_context = Cipher(
        algorithms.AES(key),
        modes.GCM(os.urandom(12)),  # 96-bit nonce for GCM
        backend=default_backend()
    ).encryptor()
    ciphertext = aes_context.update(data.encode()) + aes_context.finalize()
    return (aes_context.tag, aes_context.nonce, ciphertext)

# Decrypt data using AES-GCM
def decrypt_data(key, tag, nonce, ciphertext):
    aes_context = Cipher(
        algorithms.AES(key),
        modes.GCM(nonce, tag),
        backend=default_backend()
    ).decryptor()
    return aes_context.update(ciphertext) + aes_context.finalize()

# Store the encrypted credentials in a SQLite database
def store_credentials(db_name, table_name, username, password, key):
    conn = sqlite3.connect(db_name)
    c = conn.cursor()

    # Ensure the table exists
    c.execute(f'''
        CREATE TABLE IF NOT EXISTS {table_name} (
            id INTEGER PRIMARY KEY,
            username_tag BLOB,
            username_nonce BLOB,
            username_ciphertext BLOB,
            password_tag BLOB,
            password_nonce BLOB,
            password_ciphertext BLOB
        )
    ''')

    # Encrypt both username and password
    username_tag, username_nonce, username_ciphertext = encrypt_data(key, username)
    password_tag, password_nonce, password_ciphertext = encrypt_data(key, password)

    # Insert into the database
    c.execute(f'''
        INSERT INTO {table_name} (username_tag, username_nonce, username_ciphertext, password_tag, password_nonce, password_ciphertext)
        VALUES (?, ?, ?, ?, ?, ?)
    ''', (username_tag, username_nonce, username_ciphertext, password_tag, password_nonce, password_ciphertext))
    conn.commit()
    conn.close()

# Retrieve and decrypt credentials from the database
def retrieve_credentials(db_name, table_name, key):
    conn = sqlite3.connect(db_name)
    c = conn.cursor()
    c.execute(f'SELECT * FROM {table_name} LIMIT 1')
    row = c.fetchone()
    conn.close()
    if row:
        _, username_tag, username_nonce, username_ciphertext, password_tag, password_nonce, password_ciphertext = row
        username = decrypt_data(key, username_tag, username_nonce, username_ciphertext).decode()
        password = decrypt_data(key, password_tag, password_nonce, password_ciphertext).decode()
        return username, password
    return None, None

# Example usage
db_name = 'credentials.db'
table_name = 'credentials'
enc_key = generate_key()

# Store credentials
store_credentials(db_name, table_name, 'my_username', 'my_secure_password', enc_key)

# Retrieve credentials
username, password = retrieve_credentials(db_name, table_name, enc_key)
print(f'Username: {username}, Password: {password}')