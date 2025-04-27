import sqlite3
from cryptography.fernet import Fernet

# Generate a key for encryption and decryption (do this once and save the key securely!)
def generate_key():
    key = Fernet.generate_key()
    with open('secret.key', 'wb') as key_file:
        key_file.write(key)

# Load the key from the key file
def load_key():
    with open('secret.key', 'rb') as key_file:
        return key_file.read()

# Encrypt the credentials
def encrypt_credentials(username, password, key):
    fernet = Fernet(key)
    encrypted_username = fernet.encrypt(username.encode())
    encrypted_password = fernet.encrypt(password.encode())
    return encrypted_username, encrypted_password

# Decrypt the credentials
def decrypt_credentials(encrypted_username, encrypted_password, key):
    fernet = Fernet(key)
    decrypted_username = fernet.decrypt(encrypted_username).decode()
    decrypted_password = fernet.decrypt(encrypted_password).decode()
    return decrypted_username, decrypted_password

# Store encrypted credentials in the SQLite database
def store_credentials(db_path, username, password, key):
    encrypted_username, encrypted_password = encrypt_credentials(username, password, key)
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("CREATE TABLE IF NOT EXISTS credentials (id INTEGER PRIMARY KEY, username BLOB, password BLOB)")
    cursor.execute("INSERT INTO credentials (username, password) VALUES (?, ?)", (encrypted_username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve and decrypt credentials from the SQLite database
def get_credentials(db_path, key):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT username, password FROM credentials WHERE id=1")  # Assuming single user
    encrypted_username, encrypted_password = cursor.fetchone()
    conn.close()
    return decrypt_credentials(encrypted_username, encrypted_password, key)

# Usage example
DB_PATH = 'credentials.db'

# Generate and save the key (only do this once and comment it out afterwards)
# generate_key()

# Load the encryption key
encryption_key = load_key()

# Store the credentials
# store_credentials(DB_PATH, 'my_username', 'my_secure_password', encryption_key)

# Retrieve credentials
username, password = get_credentials(DB_PATH, encryption_key)
print(f"Retrieved Username: {username}")
print(f"Retrieved Password: {password}")