import base64
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad

def encrypt_aes_cbc(msg, password, iv):
    """Encrypts a message using AES-128-CBC with PKCS7 padding."""
    key = bytes.fromhex(password)
    iv_bytes = bytes.fromhex(iv)
    cipher = AES.new(key, AES.MODE_CBC, iv_bytes)
    padded_msg = pad(msg.encode('utf-8'), AES.block_size)
    encrypted_msg = cipher.encrypt(padded_msg)
    encoded_msg = base64.b64encode(encrypted_msg).decode('utf-8')
    return encoded_msg

def decrypt_aes_cbc(encoded_msg, password, iv):
    """Decrypts a message encrypted with AES-128-CBC with PKCS7 padding."""
    key = bytes.fromhex(password)
    iv_bytes = bytes.fromhex(iv)
    cipher = AES.new(key, AES.MODE_CBC, iv_bytes)
    encrypted_msg = base64.b64decode(encoded_msg)
    decrypted_padded_msg = cipher.decrypt(encrypted_msg)
    decrypted_msg = unpad(decrypted_padded_msg, AES.block_size).decode('utf-8')
    return decrypted_msg


iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

encrypted_message = encrypt_aes_cbc(msg, password, iv)

print(f"IV: {iv}")
print(f"PWD: {password}")
print(f"MSG: {msg}")
print(f"OUT: {encrypted_message}")

decrypted_message = decrypt_aes_cbc(encrypted_message, password, iv)
print(f"Decrypted: {decrypted_message}")

# Install pycryptodome:
# pip install pycryptodome