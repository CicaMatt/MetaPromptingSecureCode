from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import base64
import os

def encrypt_aes_128_cbc_base64(msg, key, iv):
    # Convert the input parameters from hex to bytes
    key_bytes = bytes.fromhex(key)
    iv_bytes = bytes.fromhex(iv)

    # Ensure the message is a multiple of block size (16 bytes for AES)
    # PKCS7 padding or similar can be used here, but we'll keep it simple.
    padded_msg = msg + (16 - len(msg) % 16) * chr(16 - len(msg) % 16)
    padded_msg_bytes = padded_msg.encode()

    # Create a Cipher object
    cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv_bytes), backend=default_backend())
    encryptor = cipher.encryptor()

    # Perform the encryption
    encrypted_bytes = encryptor.update(padded_msg_bytes) + encryptor.finalize()

    # Encode the result in base64
    encrypted_base64_bytes = base64.b64encode(encrypted_bytes)
    encrypted_base64 = encrypted_base64_bytes.decode()

    return encrypted_base64

# Input parameters
iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Encrypt the message
out = encrypt_aes_128_cbc_base64(msg, password, iv)

# Print results
print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {out}")