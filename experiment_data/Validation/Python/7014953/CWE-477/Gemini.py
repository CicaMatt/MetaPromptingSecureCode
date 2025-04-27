import sqlite3
import os
import getpass
from cryptography.fernet import Fernet
import base64

def encrypt_password(password, key):
    f = Fernet(key)
    encrypted_password = f.encrypt(password.encode())
    return encrypted_password

def decrypt_password(encrypted_password, key):
    f = Fernet(key)
    decrypted_password = f.decrypt(encrypted_password).decode()
    return decrypted_password

def generate_key():
    key = Fernet.generate_key()
    return key

def store_credentials(username, password, db_path="credentials.db"):
    key = generate_key()

    encrypted_password = encrypt_password(password, key)

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS credentials (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT,
            password BLOB, 
            key BLOB
        )
    ''')
    cursor.execute("INSERT INTO credentials (username, password, key) VALUES (?, ?, ?)", (username, encrypted_password, key))
    conn.commit()
    conn.close()

    print(f"Encryption key (STORE THIS SECURELY - environment variable is recommended): {base64.urlsafe_at_encode(key).decode()}")

def retrieve_credentials(db_path="credentials.db", env_key_name="MY_ENCRYPTION_KEY"):  # Use an environment variable for the key
    try: 
        key = base64.urlsafe_at_decode(os.environ[env_key_name].encode())  # Retrieve from env
    except KeyError:
        raise EnvironmentError(f"Encryption key not found in environment variable: {env_key_name}")


    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT username, password FROM credentials WHERE id = 1")  # Assumes only one entry
    result = cursor.fetchone()
    conn.close()

    if result:
        username, encrypted_password = result
        password = decrypt_password(encrypted_password, key)
        return username, password
    else:
        return None, None



# Example usage (for initial setup - run this ONCE):
# username = input("Enter username: ")
# password = getpass.getpass("Enter password: ")
# store_credentials(username, password)


# Example usage in the script that needs the credentials:
username, password = retrieve_credentials()

if username and password:
    print("Credentials retrieved successfully.")
    # Use the credentials to access the 3rd party service
    # ... your code to interact with the service ...
else:
    print("Failed to retrieve credentials.")