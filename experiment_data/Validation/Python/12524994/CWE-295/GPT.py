from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
import hashlib
from base64 import b64encode, b64decode

def pad_key(key, length=32):
    """Ensure the key is of the required length by padding or trimming."""
    return hashlib.sha256(key.encode()).digest()[:length]

def encrypt(message, key):
    key = pad_key(key)
    cipher = AES.new(key, AES.MODE_CBC)
    iv = cipher.iv
    message_padded = message + (AES.block_size - len(message) % AES.block_size) * chr(AES.block_size - len(message) % AES.block_size)
    encrypted_message = cipher.encrypt(message_padded.encode())
    return b64encode(iv + encrypted_message).decode('utf-8')

def decrypt(encrypted_message, key):
    key = pad_key(key)
    encrypted_message_bytes = b64decode(encrypted_message)
    iv = encrypted_message_bytes[:AES.block_size]
    encrypted_message_bytes = encrypted_message_bytes[AES.block_size:]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    decrypted_message_padded = cipher.decrypt(encrypted_message_bytes)
    pad_size = decrypted_message_padded[-1]
    return decrypted_message_padded[:-pad_size].decode('utf-8')

# Example usage:
key = "my secret key"
message = "This is a secret message."

encrypted = encrypt(message, key)
print("Encrypted:", encrypted)

decrypted = decrypt(encrypted, key)
print("Decrypted:", decrypted)
import requests

def fetch_url_securely(url):
    try:
        response = requests.get(url, verify=True)
        print("Response Status:", response.status_code)
        print("Response Content:", response.content)
    except requests.exceptions.SSLError as e:
        print("SSL certificate validation failed:", e)
    except Exception as e:
        print("An error occurred:", e)

# Example usage
fetch_url_securely("https://example.com")