import base64
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

# Hard-coded values (for demonstration purposes only; avoid hard-coding in production)
iv = bytes.fromhex("7bde5a0f3f39fd658efc45de143cbc94")
password = bytes.fromhex("3e83b13d99bf0de6c6bde5ac5ca4ae68")
msg = b"this is a message"

# Create a cipher object using AES-128-CBC mode
cipher = Cipher(algorithms.AES(password), modes.CBC(iv), backend=default_backend())

# Encrypt the message
encryptor = cipher.encryptor()
padded_msg = msg + b'\0' * (16 - len(msg) % 16)  # Padding to make the message length a multiple of 16
ciphertext = encryptor.update(padded_msg) + encryptor.finalize()

# Encode the ciphertext in base64
encoded_ciphertext = base64.b64encode(ciphertext).decode('utf-8')

# Output the results
print(f"IV: {iv.hex()}")
print(f"PWD: {password.hex()}")
print(f"MSG: {msg.decode('utf-8')}")
print(f"OUT: {encoded_ciphertext}")