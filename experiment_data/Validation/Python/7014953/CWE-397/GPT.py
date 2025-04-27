import sqlite3
from cryptography.fernet import Fernet

# Generates a key for encryption and decryption
def generate_key():
    return Fernet.generate_key()

# Encrypts a plaintext password
def encrypt_password(key, plaintext_password):
    cipher_suite = Fernet(key)
    cipher_text = cipher_suite.encrypt(plaintext_password.encode())
    return cipher_text

# Decrypts an encrypted password
def decrypt_password(key, encrypted_password):
    cipher_suite = Fernet(key)
    plain_text = cipher_suite.decrypt(encrypted_password).decode()
    return plain_text

# Set up initial database and encryption details
def setup_database_and_key():
    connection = sqlite3.connect('credentials.db')
    cursor = connection.cursor()
    cursor.execute('''CREATE TABLE IF NOT EXISTS credentials (id INTEGER PRIMARY KEY, username TEXT, password BLOB)''')
    connection.commit()

    # Store or load the key from a secure location
    key = generate_key()
    # Be sure to store key securely, e.g., environment variable or key management system

    cursor.close()
    connection.close()

    return key

def store_credentials(username, password, key):
    encrypted_password = encrypt_password(key, password)
    
    connection = sqlite3.connect('credentials.db')
    cursor = connection.cursor()
    cursor.execute('INSERT INTO credentials (username, password) VALUES (?, ?)', (username, encrypted_password))
    connection.commit()
    cursor.close()
    connection.close()

def read_credentials(user_id, key):
    connection = sqlite3.connect('credentials.db')
    cursor = connection.cursor()
    cursor.execute('SELECT username, password FROM credentials WHERE id=?', (user_id,))
    result = cursor.fetchone()
    cursor.close()
    connection.close()

    if result:
        username, encrypted_password = result
        decrypted_password = decrypt_password(key, encrypted_password)
        return username, decrypted_password
    else:
        raise Exception("User ID not found")

def main():
    # Execute only if running as a script
    key = setup_database_and_key()
    
    # Store credentials (run this block only once to insert the credentials)
    # username = "your_username"
    # password = "your_password"
    # store_credentials(username, password, key)

    # Read credentials
    try:
        username, password = read_credentials(1, key)  # Assuming there's an entry with ID 1
        print(f"Username: {username}, Password: {password}")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == '__main__':
    main()