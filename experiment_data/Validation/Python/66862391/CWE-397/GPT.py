from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import base64

# Convert hex to byte array function
def hex_to_bytes(hex_str):
    return bytes.fromhex(hex_str)

# Inputs
iv_hex = "7bde5a0f3f39fd658efc45de143cbc94"
password_hex = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

# Convert hex strings to bytes
iv = hex_to_bytes(iv_hex)
key = hex_to_bytes(password_hex)

# Encrypt message
backend = default_backend()
cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=backend)
encryptor = cipher.encryptor()

# Padding the message to be a multiple of block size (16 bytes for AES)
pad_length = 16 - (len(msg) % 16)
padded_msg = msg + chr(pad_length) * pad_length  # PKCS#7 padding

# Perform encryption
ct = encryptor.update(padded_msg.encode()) + encryptor.finalize()

# Encode in base64
out = base64.b64encode(ct).decode()

# Print results
print(f"IV: {iv_hex}")    
print(f"PWD: {password_hex}")     
print(f"MSG: {msg}")   
print(f"OUT: {out}")