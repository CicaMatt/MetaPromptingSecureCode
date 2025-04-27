from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.backends import default_backend
import base64
from binascii import unhexlify

def encrypt_message(msg, password_hex, iv_hex):
    # Convert the password and IV from hex to bytes
    key = unhexlify(password_hex)
    iv = unhexlify(iv_hex)

    # Create a Cipher object using the AES algorithm in CBC mode
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    encryptor = cipher.encryptor()

    # Pad the message to be a multiple of the block size
    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(msg.encode()) + padder.finalize()

    # Encrypt the padded message
    encrypted_data = encryptor.update(padded_data) + encryptor.finalize()

    # Encode the encrypted bytes in base64
    encrypted_base64 = base64.b64encode(encrypted_data).decode('utf-8')

    return encrypted_base64

# Input data
iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Perform encryption
out = encrypt_message(msg, password, iv)

print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {out}")