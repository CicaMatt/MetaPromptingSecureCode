import sqlite3
from cryptography.fernet import Fernet
import os

# Generate a key for encryption (store this securely)
def generate_key():
    return Fernet.generate_key()

# Encrypt the password
def encrypt_password(password, key):
    f = Fernet(key)
    return f.encrypt(password.encode())

# Decrypt the password
def decrypt_password(encrypted_password, key):
    f = Fernet(key)
    return f.decrypt(encrypted_password).decode()

# Store credentials in SQLite database
def store_credentials(db_path, username, encrypted_password):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS credentials
                      (id INTEGER PRIMARY KEY, username TEXT, password BLOB)''')
    cursor.execute("INSERT INTO credentials (username, password) VALUES (?, ?)", 
                  (username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve credentials from SQLite database
def retrieve_credentials(db_path):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT username, password FROM credentials ORDER BY id DESC LIMIT 1")
    result = cursor.fetchone()
    conn.close()
    return result

# Main function to demonstrate usage
def main():
    db_path = 'credentials.db'
    key_path = 'encryption_key.key'

    # Generate and save the encryption key (only do this once)
    if not os.path.exists(key_path):
        key = generate_key()
        with open(key_path, 'wb') as key_file:
            key_file.write(key)
    else:
        with open(key_path, 'rb') as key_file:
            key = key_file.read()

    # Example credentials
    username = 'my_username'
    password = 'my_password'

    # Encrypt the password
    encrypted_password = encrypt_password(password, key)

    # Store the encrypted credentials in the database
    store_credentials(db_path, username, encrypted_password)

    # Retrieve and decrypt the credentials
    stored_username, stored_encrypted_password = retrieve_credentials(db_path)
    decrypted_password = decrypt_password(stored_encrypted_password, key)

    print(f"Stored Username: {stored_username}")
    print(f"Decrypted Password: {decrypted_password}")

if __name__ == "__main__":
    main()