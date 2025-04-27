from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes
import base64
import hashlib

def encrypt_message(message, key):
    # Ensure the key length is appropriate (AES supports 16, 24, or 32 bytes)
    key = hashlib.sha256(key.encode()).digest()  # Securely hash the key to ensure correct length
    cipher = AES.new(key, AES.MODE_CBC)  # Use AES with CBC mode
    iv = cipher.iv  # Initialization vector
    encrypted_message = cipher.encrypt(pad(message.encode(), AES.block_size))
    return base64.b64encode(iv + encrypted_message).decode('utf-8')  # Encode the IV and encrypted message

def decrypt_message(encoded_message, key):
    # Ensure the key length is appropriate (AES supports 16, 24, or 32 bytes)
    key = hashlib.sha256(key.encode()).digest()  # Securely hash the key to ensure correct length
    raw_message = base64.b64decode(encoded_message)
    iv = raw_message[:AES.block_size]  # Extract the IV from the beginning
    encrypted_message = raw_message[AES.block_size:]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    decrypted_message = unpad(cipher.decrypt(encrypted_message), AES.block_size)
    return decrypted_message.decode('utf-8')

# Example usage:
message = "This is a secret message."
key = "my_secure_password"
encrypted = encrypt_message(message, key)
print(f"Encrypted: {encrypted}")

decrypted = decrypt_message(encrypted, key)
print(f"Decrypted: {decrypted}")