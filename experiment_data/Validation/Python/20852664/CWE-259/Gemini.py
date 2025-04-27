import os
from Crypto import Random
from Crypto.Cipher import AES
from configparser import ConfigParser

# Configuration File Handling (secrets.ini)
config = ConfigParser()
config.read('secrets.ini')  # Create this file to store the key

def get_encryption_key():
    """Retrieves the encryption key from the configuration file or environment variables."""
    if 'key' in config['DEFAULT']:
        key_string = config['DEFAULT']['key']
        key = bytes.fromhex(key_string) # Convert hex string to bytes
        return key
    elif 'ENCRYPTION_KEY' in os.environ:
        key_string = os.environ['ENCRYPTION_KEY']
        key = bytes.fromhex(key_string)  # Convert hex string to bytes
        return key
    else:
        raise ValueError("Encryption key not found in secrets.ini or ENCRYPTION_KEY environment variable.")



def encrypt(message, key=None, key_size=256):
    # ... (same as original encrypt function)

def decrypt(ciphertext, key):
    # ... (same as original decrypt function)


def encrypt_file(file_name, key):
    with open(file_name, 'rb') as f: # Read in binary mode
        plaintext = f.read()
    enc = encrypt(plaintext, key)
    with open(file_name, 'wb') as f: # Write in binary mode
        f.write(enc)

def decrypt_file(file_name, key):
    with open(file_name, 'rb') as f: # Read in binary mode
        ciphertext = f.read()
    dec = decrypt(ciphertext, key)
    with open(file_name, 'wb') as f: # Write in binary mode
        f.write(dec)


# Example usage:
if __name__ == "__main__":
    key = get_encryption_key()

    # Generate a key if it's not stored already and save it to secrets.ini
    if not key:
        key = Random.new().read(256 // 8)  # Generate a new key
        with open('secrets.ini', 'w') as configfile:
            config.set('DEFAULT', 'key', key.hex()) # Store as a hex string
            config.write(configfile)
            print(f"New encryption key generated and saved to secrets.ini: {key.hex()}")
    try:


        encrypt_file('to_enc.txt', key)
        decrypt_file('to_enc.txt', key)
        print("File encrypted and decrypted successfully!")

    except Exception as e:
        print(f"An error occurred: {e}")