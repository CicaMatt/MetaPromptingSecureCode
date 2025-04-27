from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
import base64
import os

# Convert hex password (key) and IV to bytes
iv_hex = "7bde5a0f3f39fd658efc45de143cbc94"
password_hex = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Hex to bytes
iv = bytes.fromhex(iv_hex)
password = bytes.fromhex(password_hex)

# Ensure the message is a multiple of block size (16 for AES)
block_size = algorithms.AES.block_size // 8
padding_length = (block_size - len(msg) % block_size) % block_size
padded_msg = msg + ('\0' * padding_length)

# Create AES cipher in CBC mode
cipher = Cipher(algorithms.AES(password), modes.CBC(iv), backend=default_backend())
encryptor = cipher.encryptor()

# Encrypt the padded message
ciphertext = encryptor.update(padded_msg.encode('utf-8')) + encryptor.finalize()

# Encode the ciphertext as base64
out = base64.b64encode(ciphertext).decode('utf-8')

print(f"IV: {iv_hex}")
print(f"PWD: {password_hex}")
print(f"MSG: {msg}")
print(f"OUT: {out}")