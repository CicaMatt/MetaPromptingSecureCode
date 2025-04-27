import keyring
import sqlite3

SERVICE_NAME = 'my_service'
USERNAME = 'user123'

def set_credentials(username, password):
    # Set credentials using the system's keyring
    keyring.set_password(SERVICE_NAME, username, password)

def get_credentials(username):
    # Get credentials from the system's keyring
    password = keyring.get_password(SERVICE_NAME, username)
    return username, password

def create_and_connect_db(db_name):
    # Connect to an SQLite database (or create it if it doesn't exist)
    connection = sqlite3.connect(db_name)
    return connection

def setup_database(connection):
    # Setup your SQLite database with necessary tables, exclude sensitive data
    cursor = connection.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS user_data (
            id INTEGER PRIMARY KEY,
            data TEXT NOT NULL
        )
    ''')
    connection.commit()

def insert_data(connection, data):
    # Insert non-sensitive data into SQLite database
    cursor = connection.cursor()
    cursor.execute('INSERT INTO user_data (data) VALUES (?)', (data,))
    connection.commit()

def fetch_data(connection):
    # Fetch non-sensitive data from the database
    cursor = connection.cursor()
    cursor.execute('SELECT * FROM user_data')
    rows = cursor.fetchall()
    return rows

def main():
    # Example usage: set credentials
    set_credentials(USERNAME, 'secure_password_here')

    # Retrieve credentials for use
    username, password = get_credentials(USERNAME)
    print(f"Retrieved credentials: {username}, {password}")

    # Setup SQLite and enter some dummy data
    db_conn = create_and_connect_db('example.db')
    setup_database(db_conn)
    insert_data(db_conn, 'Sample data')

    # Fetch and print non-sensitive data
    rows = fetch_data(db_conn)
    for row in rows:
        print(row)

    # Remember to close the connection when done
    db_conn.close()

if __name__ == "__main__":
    main()