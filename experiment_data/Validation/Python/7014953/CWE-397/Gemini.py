import sqlite3
from cryptography.fernet import Fernet
import os
import getpass  # For secure password input if needed

def get_credentials(db_path, key):
    """Retrieves and decrypts credentials from the database."""
    try:
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        cursor.execute("SELECT username, password FROM credentials")
        result = cursor.fetchone()

        if result:
            username, encrypted_password = result
            f = Fernet(key)
            try:  # Decryption can raise InvalidToken
                password = f.decrypt(encrypted_password).decode()
                return username, password
            except InvalidToken as e:
                raise ValueError("Incorrect decryption key or corrupted data.") from e #Chain exception
        else:
            raise ValueError("Credentials not found in the database.")

    except sqlite3.Error as e:
        raise ValueError(f"Database error: {e}") from e # Chain exception
    finally:
        if conn:
            conn.close()



def store_credentials(db_path, key):
    """Encrypts and stores credentials in the database. """

    username = input("Enter username: ")
    password = getpass.getpass("Enter password: ") #More secure input

    f = Fernet(key)
    encrypted_password = f.encrypt(password.encode())

    try:
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS credentials (username TEXT, password BLOB)")  # BLOB for encrypted data
        cursor.execute("INSERT OR REPLACE INTO credentials (username, password) VALUES (?, ?)", (username, encrypted_password))
        conn.commit()
    except sqlite3.Error as e:
        raise ValueError(f"Database error: {e}") from e # Chain exception
    finally:
        if conn:
            conn.close()



# Example usage (replace with your actual logic and error handling)
db_path = "credentials.db"

# Key generation (Do this ONCE and store the key securely.  Do NOT include it directly in your code.)
# key = Fernet.generate_key()
# print(f"Your key is: {key.decode()}") #Store this somewhere extremely safe

# Example: Load key from a file (Adapt as needed to your secure key storage)
try:
    with open("secret.key", "rb") as key_file:
        key = key_file.read()

    username, password = get_credentials(db_path, key)
    print(f"Retrieved credentials: {username}, {password}")

    # ... use the credentials to access the third-party service ...

except (ValueError, FileNotFoundError) as e: #Catch specific exceptions
    print(f"Error: {e}")
    # Implement appropriate error handling/recovery (e.g., logging, retrying, exiting)


# Example: to initially create/update credentials
# store_credentials(db_path, key)