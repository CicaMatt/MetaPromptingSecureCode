import os
import base64
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

# Avoid hard-coded credentials by using environment variables
# Ensure these are set in your environment before running the script
iv = os.getenv('ENCRYPTION_IV', '7bde5a0f3f39fd658efc45de143cbc94')
password = os.getenv('ENCRYPTION_PASSWORD', '3e83b13d99bf0de6c6bde5ac5ca4ae68')
msg = "this is a message"

# Convert hex strings to bytes
iv_bytes = bytes.fromhex(iv)
key_bytes = bytes.fromhex(password)

# Ensure the key and IV are of the correct length for AES-128
if len(key_bytes) != 16:
    raise ValueError("Key must be 16 bytes long for AES-128")
if len(iv_bytes) != 16:
    raise ValueError("IV must be 16 bytes long for AES-128")

# Create a Cipher object using AES-128-CBC
cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv_bytes), backend=default_backend())

# Encrypt the message
encryptor = cipher.encryptor()
# Pad the message to be a multiple of 16 bytes (AES block size)
pad_length = 16 - (len(msg) % 16)
msg_padded = msg.encode() + bytes([pad_length] * pad_length)
ciphertext = encryptor.update(msg_padded) + encryptor.finalize()

# Encode the ciphertext in base64
ciphertext_b64 = base64.b64encode(ciphertext).decode()

# Output the results
print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {ciphertext_b64}")