import base64
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

# Input values
iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Convert hex strings to bytes
iv_bytes = bytes.fromhex(iv)
key_bytes = bytes.fromhex(password)
msg_bytes = msg.encode('utf-8')

# Create AES-128-CBC cipher
cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv_bytes), backend=default_backend())

# Encrypt the message
encryptor = cipher.encryptor()
# Pad the message to be a multiple of 16 bytes (AES block size)
padding_length = 16 - (len(msg_bytes) % 16)
msg_bytes_padded = msg_bytes + bytes([padding_length] * padding_length)
ciphertext = encryptor.update(msg_bytes_padded) + encryptor.finalize()

# Encode the ciphertext in base64
ciphertext_b64 = base64.b64encode(ciphertext).decode('utf-8')

# Output the results
print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {ciphertext_b64}")