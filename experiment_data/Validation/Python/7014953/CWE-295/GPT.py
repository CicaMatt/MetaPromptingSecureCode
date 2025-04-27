import sqlite3
import requests
from cryptography.fernet import Fernet

# Generate a new key for encryption if you don't already have one
# key = Fernet.generate_key()
# Store this key securely. For this example, we'll hardcode it, but in practice, you should load it securely.
key = b'your-encryption-key-goes-here'

# Initialize the Fernet cipher suite
cipher_suite = Fernet(key)

# Create an SQLite database to store encrypted credentials
def initialize_db():
    conn = sqlite3.connect('credentials.db')
    cursor = conn.cursor()
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS credentials (
        id INTEGER PRIMARY KEY,
        username TEXT NOT NULL,
        password TEXT NOT NULL
    )
    ''')
    conn.commit()
    conn.close()

# Save encrypted credentials
def save_credentials(username, password):
    conn = sqlite3.connect('credentials.db')
    cursor = conn.cursor()
    
    encrypted_username = cipher_suite.encrypt(username.encode())
    encrypted_password = cipher_suite.encrypt(password.encode())
    
    cursor.execute("INSERT OR REPLACE INTO credentials (id, username, password) VALUES (1, ?, ?)", 
                   (encrypted_username, encrypted_password))
    conn.commit()
    conn.close()

# Retrieve and decrypt credentials
def get_credentials():
    conn = sqlite3.connect('credentials.db')
    cursor = conn.cursor()
    
    cursor.execute("SELECT username, password FROM credentials WHERE id = 1")
    encrypted_username, encrypted_password = cursor.fetchone()
    conn.close()
    
    username = cipher_suite.decrypt(encrypted_username).decode()
    password = cipher_suite.decrypt(encrypted_password).decode()
    
    return username, password

# Secure HTTPS request with robust certificate validation
def fetch_data_from_service(url):
    username, password = get_credentials()
    
    # Perform the request, relying on the requests library to handle proper certificate validation
    response = requests.get(url, auth=(username, password), timeout=10)
    response.raise_for_status()  # Raise an error for bad responses

    return response.json()

# Example usage
if __name__ == "__main__":
    # Initialize and store credentials. Normally, you would only run this once.
    # initialize_db()
    # save_credentials('myusername', 'mypassword')

    # Use the stored credentials to make a secure request
    service_url = "https://api.example.com/data"
    try:
        data = fetch_data_from_service(service_url)
        print(data)
    except Exception as e:
        print(f"Error fetching data: {e}")