import sqlite3
from cryptography.fernet import Fernet
import os

# Generate or load a key for encryption
def load_or_generate_key(key_file='secret.key'):
    if os.path.exists(key_file):
        with open(key_file, 'rb') as f:
            key = f.read()
    else:
        key = Fernet.generate_key()
        with open(key_file, 'wb') as f:
            f.write(key)
    return key

# Encrypt data
def encrypt_data(data, key):
    f = Fernet(key)
    return f.encrypt(data.encode())

# Decrypt data
def decrypt_data(encrypted_data, key):
    f = Fernet(key)
    return f.decrypt(encrypted_data).decode()

# Initialize the SQLite database and table
def init_db(db_file='credentials.db'):
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS credentials (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username BLOB NOT NULL,
            password BLOB NOT NULL
        )
    ''')
    conn.commit()
    return conn

# Store credentials in the database
def store_credentials(username, password, conn, key):
    encrypted_username = encrypt_data(username, key)
    encrypted_password = encrypt_data(password, key)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO credentials (username, password)
        VALUES (?, ?)
    ''', (encrypted_username, encrypted_password))
    conn.commit()

# Retrieve credentials from the database
def retrieve_credentials(conn, key):
    cursor = conn.cursor()
    cursor.execute('SELECT username, password FROM credentials ORDER BY id DESC LIMIT 1')
    row = cursor.fetchone()
    if row:
        encrypted_username, encrypted_password = row
        username = decrypt_data(encrypted_username, key)
        password = decrypt_data(encrypted_password, key)
        return username, password
    return None, None

# Main function to demonstrate usage
def main():
    key = load_or_generate_key()
    conn = init_db()

    # Example: Store credentials
    username = "your_username"
    password = "your_password"
    store_credentials(username, password, conn, key)

    # Example: Retrieve credentials
    retrieved_username, retrieved_password = retrieve_credentials(conn, key)
    if retrieved_username and retrieved_password:
        print(f"Retrieved Username: {retrieved_username}")
        print(f"Retrieved Password: {retrieved_password}")
    else:
        print("No credentials found.")

    conn.close()

if __name__ == "__main__":
    main()