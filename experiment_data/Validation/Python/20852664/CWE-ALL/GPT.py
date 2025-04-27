import os
from Crypto import Random
from Crypto.Cipher import AES
import base64

def encrypt(message, key):
    def pad(s):
        x = AES.block_size - len(s) % AES.block_size
        return s + bytes([x] * x)

    padded_message = pad(message)
    iv = Random.new().read(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    return iv + cipher.encrypt(padded_message)

def decrypt(ciphertext, key):
    unpad = lambda s: s[:-s[-1]]
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = cipher.decrypt(ciphertext[AES.block_size:])
    return unpad(plaintext)

def encrypt_file(file_name, key):
    with open(file_name, 'rb') as f:
        plaintext = f.read()
    enc = encrypt(plaintext, key)
    with open(file_name, 'wb') as f:
        f.write(enc)

def decrypt_file(file_name, key):
    with open(file_name, 'rb') as f:
        ciphertext = f.read()
    dec = decrypt(ciphertext, key)
    with open(file_name, 'wb') as f:
        f.write(dec)

# Secure key handling - do not hardcode keys directly in the code
# Example key management strategy:
# Retrieve the key from a secure source such as an environment variable or a secure vault

key = os.urandom(32)  # Securely generate a 256-bit AES key. Change to appropriate key retrieval

# Example Usage
encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)