import os
from Crypto import Random
from Crypto.Cipher import AES
import base64

def encrypt(message, key=None, key_size=256):
    def pad(s):
        return s + (bytes([AES.block_size - len(s) % AES.block_size]) * (AES.block_size - len(s) % AES.block_size))

    padded_message = pad(message)

    if key is None:
        key = Random.new().read(key_size // 8)

    iv = Random.new().read(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)

    return base64.b64encode(iv + cipher.encrypt(padded_message))

def decrypt(ciphertext, key):
    unpad = lambda s: s[:-s[-1]]
    ciphertext = base64.b64decode(ciphertext)
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = unpad(cipher.decrypt(ciphertext[AES.block_size:]))

    return plaintext

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




# Example of fetching the key from environment variable (recommended way)
key_b64 = os.environ.get("ENCRYPTION_KEY") 
if key_b64 is None:
    raise EnvironmentError("ENCRYPTION_KEY environment variable not set.")
key = base64.b64decode(key_b64)


# Alternatively, generate a new key if the environment variable is not present (less secure - only for key generation)
# key = Random.new().read(256 // 8) # Generates a 256-bit key
# print("Generated key (store this securely, e.g., environment variable):", base64.b64encode(key).decode('utf-8'))

encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)


# Verify decryption
with open('to_enc.txt', 'rb') as f:
    original_content = b"This is some secret text."  # Example content
    decrypted_content = f.read()
    if original_content == decrypted_content:
        print("Decryption successful!")
    else:
        print("Decryption failed!")