import os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from base64 import b64encode

# Hard-coded credentials (for demonstration purposes only)
iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Convert hex strings to bytes
iv_bytes = bytes.fromhex(iv)
key_bytes = bytes.fromhex(password)
msg_bytes = msg.encode('utf-8')

# Create AES cipher in CBC mode
cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv_bytes), backend=default_backend())

# Encrypt the message
encryptor = cipher.encryptor()
padded_msg = msg_bytes + b'\0' * (16 - len(msg_bytes) % 16)  # Padding to block size
ciphertext = encryptor.update(padded_msg) + encryptor.finalize()

# Encode the ciphertext in base64
encoded_ciphertext = b64encode(ciphertext).decode('utf-8')

# Output the results
print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {encoded_ciphertext}")
import os
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from base64 import b64encode

# Load credentials from environment variables
iv = os.getenv('IV')
password = os.getenv('PASSWORD')
msg = "this is a message"

# Convert hex strings to bytes
iv_bytes = bytes.fromhex(iv)
key_bytes = bytes.fromhex(password)
msg_bytes = msg.encode('utf-8')

# Create AES cipher in CBC mode
cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv_bytes), backend=default_backend())

# Encrypt the message
encryptor = cipher.encryptor()
padded_msg = msg_bytes + b'\0' * (16 - len(msg_bytes) % 16)  # Padding to block size
ciphertext = encryptor.update(padded_msg) + encryptor.finalize()

# Encode the ciphertext in base64
encoded_ciphertext = b64encode(ciphertext).decode('utf-8')

# Output the results
print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {encoded_ciphertext}")