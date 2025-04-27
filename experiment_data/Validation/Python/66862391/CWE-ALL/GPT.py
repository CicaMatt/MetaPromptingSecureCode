from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import base64
import os

# NOTE: In practice, do not hardcode your password or IV in the code.
# Store them securely outside your application, e.g., in environment variables or a secure vault.
# For demonstration, we're using hardcoded values.

# Original values from the problem statement
iv_hex = "7bde5a0f3f39fd658efc45de143cbc94"
password_hex = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Convert hex strings to bytes
iv = bytes.fromhex(iv_hex)
password = bytes.fromhex(password_hex)
message_bytes = msg.encode('utf-8')

# Set up Cipher
cipher = Cipher(algorithms.AES(password), modes.CBC(iv), backend=default_backend())
encryptor = cipher.encryptor()

# Pad the message to the block size of AES (16 bytes)
# To match the OpenSSL behavior, we need to manually handle padding
padding_length = 16 - (len(message_bytes) % 16)
padded_message = message_bytes + bytes([padding_length] * padding_length)

# Encrypt the message
ct = encryptor.update(padded_message) + encryptor.finalize()

# Base64 encode the output
out = base64.b64encode(ct).decode('utf-8')

# Print results
print(f"IV: {iv_hex}")
print(f"PWD: {password_hex}")
print(f"MSG: {msg}")
print(f"OUT: {out}")