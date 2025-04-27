import os
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import pad, unpad
import base64
import json

def encrypt(message, key):
    iv = get_random_bytes(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    ciphertext = cipher.encrypt(pad(message, AES.block_size))
    return base64.b64encode(iv + ciphertext).decode('utf-8')


def decrypt(ciphertext, key):
    try:
        ciphertext = base64.b64decode(ciphertext)
        iv = ciphertext[:AES.block_size]
        cipher = AES.new(key, AES.MODE_CBC, iv)
        plaintext = unpad(cipher.decrypt(ciphertext[AES.block_size:]), AES.block_size)
        return plaintext.decode('utf-8') 
    except (ValueError, KeyError):
        return None


def encrypt_file(file_name, key):
    with open(file_name, 'r', encoding='utf-8') as f:  # Explicitly handle encoding
        plaintext = f.read().encode('utf-8')

    ciphertext = encrypt(plaintext, key)

    with open(file_name, 'w', encoding='utf-8') as f:
        f.write(ciphertext)


def decrypt_file(file_name, key):
    try:
        with open(file_name, 'r', encoding='utf-8') as f:
            ciphertext = f.read()

        plaintext = decrypt(ciphertext, key)
        if plaintext is None:
            raise ValueError("Decryption failed.")  # Specific exception

        with open(file_name, 'w', encoding='utf-8') as f:
            f.write(plaintext)
    except (ValueError, OSError) as e:  # Catch specific exceptions
        print(f"Error decrypting file: {e}")
        return  # Or handle the error differently


def load_key_from_file(key_file="key.json"):  # Secure key storage (example)
    try:
        with open(key_file, 'r') as f:
            data = json.load(f)
            key_base64 = data.get('key')
            if key_base64:
                return base64.b64decode(key_base64)
            else:
                raise ValueError("Key not found in JSON.")
    except (FileNotFoundError, json.JSONDecodeError, ValueError) as e:
        print(f"Error loading key: {e}")
        return None




# Example usage (replace with your key management strategy)
# For demonstration only. In real code, generate and securely store the key.
key = load_key_from_file() # or generate with: key = get_random_bytes(32)  and securely store it.
if not key:
    print("Key not available, exiting.")
    exit(1)  # Exit with error code

encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)