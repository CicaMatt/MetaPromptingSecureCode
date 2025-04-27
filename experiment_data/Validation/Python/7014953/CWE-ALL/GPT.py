import sqlite3
from cryptography.fernet import Fernet
import os
import ssl
import requests

# Path to the SQLite database
DB_PATH = 'credentials.db'
# Securely store the Fernet key (e.g., in an environment variable)
FERNET_KEY = os.environ.get('FERNET_KEY')

# Ensure the Fernet key is available
if not FERNET_KEY:
    raise ValueError("FERNET_KEY environment variable is not set. Please set it to a valid key.")

# Fernet cryptography setup
cipher = Fernet(FERNET_KEY)

def initialize_database():
    """Initialize the SQLite database and table if it doesn't exist."""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS credentials (
            id INTEGER PRIMARY KEY,
            service TEXT UNIQUE,
            username TEXT,
            password BLOB
        )
    ''')
    conn.commit()
    conn.close()

def store_credentials(service, username, password):
    """Store encrypted credentials in the database."""
    encrypted_password = cipher.encrypt(password.encode())
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        REPLACE INTO credentials(service, username, password)
        VALUES (?,?,?)
    ''', (service, username, encrypted_password))
    conn.commit()
    conn.close()

def get_credentials(service):
    """Retrieve decrypted credentials from the database."""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT username, password FROM credentials WHERE service=?', (service,))
    result = cursor.fetchone()
    conn.close()
    if result:
        username, encrypted_password = result
        return username, cipher.decrypt(encrypted_password).decode()
    else:
        raise ValueError(f"No credentials stored for service: {service}")

def make_secure_request(service_url, username, password):
    """Make a secure request to a service with proper SSL certificate check."""
    # Ensure a proper SSL context is used
    context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
    response = requests.get(service_url, auth=(username, password), verify=context)
    response.raise_for_status()
    return response.json()

# Example Usage
initialize_database()
# Store credentials (for first time setup or updates)
# store_credentials('my_service', 'example_user', 'example_password')

# Fetch and use credentials
try:
    service_username, service_password = get_credentials('my_service')
    data = make_secure_request('https://api.example.com/data', service_username, service_password)
    print(data)
except Exception as e:
    print(f"An error occurred: {e}")