import os
import sqlite3
from cryptography.fernet import Fernet
from getpass import getpass

# Function to generate and write encryption key
def generate_encryption_key():
    key = Fernet.generate_key()
    with open("encryption.key", "wb") as key_file:
        key_file.write(key)

# Function to load the encryption key
def load_encryption_key():
    return open("encryption.key", "rb").read()

# Function to encrypt data
def encrypt_data(data, key):
    fernet = Fernet(key)
    return fernet.encrypt(data.encode())

# Function to decrypt data
def decrypt_data(encrypted_data, key):
    fernet = Fernet(key)
    return fernet.decrypt(encrypted_data).decode()

# Check if encryption key exists, if not generate one
if not os.path.exists("encryption.key"):
    generate_encryption_key()

encryption_key = load_encryption_key()

# Create SQLite database connection
conn = sqlite3.connect('credentials.db')
cursor = conn.cursor()

# Create table to store credentials if it does not exist
cursor.execute('''
CREATE TABLE IF NOT EXISTS credentials (
    id INTEGER PRIMARY KEY,
    service TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL
)
''')

# Prompt user to store new credentials
service = input("Enter the service name: ")
username = input("Enter your username: ")
password = getpass("Enter your password: ")

# Encrypt the credentials
encrypted_username = encrypt_data(username, encryption_key)
encrypted_password = encrypt_data(password, encryption_key)

# Store encrypted credentials in SQLite database
cursor.execute('''
INSERT INTO credentials (service, username, password) VALUES (?, ?, ?)
''', (service, encrypted_username, encrypted_password))

conn.commit()

# Function to retrieve and decrypt credentials
def get_credentials(service_name):
    cursor.execute('''
    SELECT username, password FROM credentials WHERE service = ?
    ''', (service_name,))
    encrypted_username, encrypted_password = cursor.fetchone()
    decrypted_username = decrypt_data(encrypted_username, encryption_key)
    decrypted_password = decrypt_data(encrypted_password, encryption_key)
    return decrypted_username, decrypted_password

# Example: Retrieve credentials for a specific service
service_name = input("Enter the service name to retrieve credentials: ")
username, password = get_credentials(service_name)
print(f"Username: {username}, Password: {password}")

# Cleanup
conn.close()