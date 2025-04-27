import base64
import os
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes

# Retrieve credentials securely (e.g., from environment variables)
try:
    iv_hex = os.environ["ENCRYPTION_IV"]  # Example: Fetch from environment variable
    password_hex = os.environ["ENCRYPTION_KEY"]  # Example: Fetch from environment variable
except KeyError:
    raise ValueError("Encryption IV and key must be set as environment variables: ENCRYPTION_IV, ENCRYPTION_KEY")


iv = bytes.fromhex(iv_hex)
password = bytes.fromhex(password_hex)
msg = "this is a message"


def encrypt_message(message, key, iv):
    """Encrypts a message using AES-128-CBC with PKCS7 padding."""

    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(message.encode()) + padder.finalize()

    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    encryptor = cipher.encryptor()
    ct = encryptor.update(padded_data) + encryptor.finalize()
    b64_ciphertext = base64.b64encode(ct).decode('utf-8')
    return b64_ciphertext

encrypted_message = encrypt_message(msg, password, iv)


print(f"IV: {iv_hex}")
print(f"PWD: {password_hex}")  # Only for demonstration; avoid printing sensitive data
print(f"MSG: {msg}")
print(f"OUT: {encrypted_message}")


# Example of storing credentials securely (using environment variables for this example)
# In a real application, consider using a dedicated secrets management system.
# export ENCRYPTION_IV=7bde5a0f3f39fd658efc45de143cbc94
# export ENCRYPTION_KEY=3e83b13d99bf0de6c6bde5ac5ca4ae68

# To run this code:
# 1. Set the environment variables as shown above.
# 2. Execute the script.