import sqlite3
from cryptography.fernet import Fernet
import os

# Generate or load the encryption key
def load_or_generate_key(key_file='secret.key'):
    if os.path.exists(key_file):
        with open(key_file, 'rb') as f:
            key = f.read()
    else:
        key = Fernet.generate_key()
        with open(key_file, 'wb') as f:
            f.write(key)
    return key

# Encrypt the credentials
def encrypt_credentials(username, password, key):
    cipher_suite = Fernet(key)
    encrypted_username = cipher_suite.encrypt(username.encode())
    encrypted_password = cipher_suite.encrypt(password.encode())
    return encrypted_username, encrypted_password

# Decrypt the credentials
def decrypt_credentials(encrypted_username, encrypted_password, key):
    cipher_suite = Fernet(key)
    username = cipher_suite.decrypt(encrypted_username).decode()
    password = cipher_suite.decrypt(encrypted_password).decode()
    return username, password

# Store credentials in SQLite database
def store_credentials(db_file, encrypted_username, encrypted_password):
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS credentials
                      (id INTEGER PRIMARY KEY, username BLOB, password BLOB)''')
    cursor.execute('INSERT INTO credentials (username, password) VALUES (?, ?)',
                  (encrypted_username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve credentials from SQLite database
def retrieve_credentials(db_file):
    conn = sqlite3.connect(db_file)
    cursor = conn.cursor()
    cursor.execute('SELECT username, password FROM credentials ORDER BY id DESC LIMIT 1')
    row = cursor.fetchone()
    conn.close()
    return row

# Main function to demonstrate usage
def main():
    db_file = 'credentials.db'
    key_file = 'secret.key'
    
    # Load or generate the encryption key
    key = load_or_generate_key(key_file)
    
    # Example credentials
    username = 'my_username'
    password = 'my_password'
    
    # Encrypt and store credentials
    encrypted_username, encrypted_password = encrypt_credentials(username, password, key)
    store_credentials(db_file, encrypted_username, encrypted_password)
    
    # Retrieve and decrypt credentials
    encrypted_username, encrypted_password = retrieve_credentials(db_file)
    decrypted_username, decrypted_password = decrypt_credentials(encrypted_username, encrypted_password, key)
    
    print(f'Decrypted Username: {decrypted_username}')
    print(f'Decrypted Password: {decrypted_password}')

if __name__ == '__main__':
    main()