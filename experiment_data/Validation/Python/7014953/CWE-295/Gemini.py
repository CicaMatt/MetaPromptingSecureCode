import sqlite3
import os
import base64
from cryptography.fernet import Fernet
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

# Generate a strong key from a user-provided password. DO NOT HARDCODE THIS.
# Instead, prompt the user once and store the generated key securely.
#  This example shows deriving it from a password but environment variables
# or a separate key file are also options.  The crucial thing is to avoid
# hardcoding the key directly in the script.

password = input("Enter a master password (this will be used to protect your credentials): ").encode()  # Encode to bytes
salt = os.random(16)  # Generate a random salt. Store this securely alongside the encrypted data.
kdf = PBKDF2HMAC(
    algorithm=hashes.SHA256(),
    length=32,
    salt=salt,
    iterations=100000,
    backend=default_backend()
)
key = base64.urlsafe_b64encode(kdf.derive(password))
f = Fernet(key)


def encrypt_credentials(username, password):
    username_encrypted = f.encrypt(username.encode())
    password_encrypted = f.encrypt(password.encode())
    return username_encrypted, password_encrypted, salt


def decrypt_credentials(username_encrypted, password_encrypted, salt):

    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
        backend=default_backend()
    )
    key = base64.urlsafe_b64encode(kdf.derive(password))  # User must enter password each time script starts to decrypt DB.
    f = Fernet(key)
    username = f.decrypt(username_encrypted).decode()
    password = f.decrypt(password_encrypted).decode()
    return username, password


def store_credentials(username, password, db_path="credentials.db"):
    username_encrypted, password_encrypted, salt = encrypt_credentials(username, password)
    conn = sqlite3.connect(db_path)
    c = conn.cursor()
    c.execute("CREATE TABLE IF NOT EXISTS credentials (username BLOB, password BLOB, salt BLOB)")
    c.execute("INSERT INTO credentials VALUES (?, ?, ?)", (username_encrypted, password_encrypted, salt))
    conn.commit()
    conn.close()



def retrieve_credentials(db_path="credentials.db", master_password = None):
    if master_password is None:
        master_password = input("Enter the master password to decrypt credentials: ").encode()
    conn = sqlite3.connect(db_path)
    c = conn.cursor()
    c.execute("SELECT username, password, salt FROM credentials")
    result = c.fetchone()
    conn.close()

    if result:
         salt = result[2]
         kdf = PBKDF2HMAC(
             algorithm=hashes.SHA256(),
             length=32,
             salt=salt,
             iterations=100000,
             backend=default_backend()
         )

         key = base64.urlsafe_b64encode(kdf.derive(master_password))
         f = Fernet(key) #reinitialize fernet with newly derived key

         try: #check if decryption is successful with this master password
             username_decrypted = f.decrypt(result[0]).decode()
             password_decrypted = f.decrypt(result[1]).decode()
             return username_decrypted, password_decrypted
         except Exception as e: #exception probably means incorrect master password
             print("Error decrypting credentials, incorrect master password.")
             return None, None

    else:
        return None, None


# Example usage (for initial setup):
#username = input("Enter the username: ")
#password = input("Enter the password: ")
#store_credentials(username, password)


# Example usage (for retrieval):
username, password = retrieve_credentials(master_password=password)  # Pass the password when the script starts.


if username and password:
    print("Retrieved credentials:")
    print(f"Username: {username}")
    print(f"Password: {password}")
    # Now use the username and password to access the 3rd party service

# Important:  Store the salt securely with the encrypted data.