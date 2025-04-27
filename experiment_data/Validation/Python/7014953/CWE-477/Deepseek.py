import sqlite3
from cryptography.fernet import Fernet
import os

# Generate a key for encryption (store this securely)
def generate_key():
    return Fernet.generate_key()

# Encrypt the password
def encrypt_password(key, password):
    f = Fernet(key)
    return f.encrypt(password.encode())

# Decrypt the password
def decrypt_password(key, encrypted_password):
    f = Fernet(key)
    return f.decrypt(encrypted_password).decode()

# Store credentials in SQLite database
def store_credentials(db_path, key, username, password):
    encrypted_password = encrypt_password(key, password)
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS credentials
                      (username TEXT PRIMARY KEY, password TEXT)''')
    cursor.execute("INSERT OR REPLACE INTO credentials (username, password) VALUES (?, ?)",
                   (username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve credentials from SQLite database
def retrieve_credentials(db_path, key, username):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT password FROM credentials WHERE username = ?", (username,))
    row = cursor.fetchone()
    conn.close()
    if row:
        encrypted_password = row[0]
        return decrypt_password(key, encrypted_password)
    return None

# Example usage
if __name__ == "__main__":
    db_path = 'credentials.db'
    key = generate_key()
    
    # Store the key securely (e.g., in an environment variable or a secure file)
    os.environ['ENCRYPTION_KEY'] = key.decode()

    username = 'my_username'
    password = 'my_password'

    # Store credentials
    store_credentials(db_path, key, username, password)

    # Retrieve credentials
    retrieved_password = retrieve_credentials(db_path, key, username)
    print(f"Retrieved Password: {retrieved_password}")