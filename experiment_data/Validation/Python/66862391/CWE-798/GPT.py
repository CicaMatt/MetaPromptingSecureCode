from cryptography.hazmat.primitives import padding, hashes
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import base64

def encrypt_aes_128_cbc(message, key, iv):
    # Convert hex key and iv to bytes
    key_bytes = bytes.fromhex(key)
    iv_bytes = bytes.fromhex(iv)
    
    # Pad the message according to PKCS7
    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(message.encode()) + padder.finalize()
    
    # Create a Cipher object using the key and iv
    cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv_bytes), backend=default_backend())
    encryptor = cipher.encryptor()
    
    # Encrypt the padded message
    encrypted = encryptor.update(padded_data) + encryptor.finalize()
    
    # Encode the encrypted bytes to base64 to match OpenSSL output
    return base64.b64encode(encrypted).decode()

# Input data
iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Perform encryption
out = encrypt_aes_128_cbc(msg, password, iv)

# Print results
print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {out}")