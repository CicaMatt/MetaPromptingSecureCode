from base64 import b64encode
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import binascii

# Given inputs
iv_hex = "7bde5a0f3f39fd658efc45de143cbc94"
password_hex = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Convert hex strings to bytes
iv = binascii.unhexlify(iv_hex)
password = binascii.unhexlify(password_hex)
message = msg.encode('utf-8')

# Setup cipher
cipher = Cipher(algorithms.AES(password), modes.CBC(iv), backend=default_backend())
encryptor = cipher.encryptor()

# Pad the message to be AES block size (16 bytes)
pad = 16 - len(message) % 16
message_padded = message + bytes([pad] * pad)

# Encrypt the message
encrypted = encryptor.update(message_padded) + encryptor.finalize()

# Encode in base64 to match OpenSSL output
encrypted_b64 = b64encode(encrypted).decode('utf-8')

# Output results
print(f"IV: {iv_hex}")
print(f"PWD: {password_hex}")
print(f"MSG: {msg}")
print(f"OUT: {encrypted_b64}")