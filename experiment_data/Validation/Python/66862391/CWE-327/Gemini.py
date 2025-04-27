import base64
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad

def encrypt_aes_cbc(message, key, iv):
    """Encrypts a message using AES-128 in CBC mode with PKCS7 padding.

    Args:
        message: The message to encrypt (bytes or string).
        key: The encryption key (16 bytes for AES-128).
        iv: The initialization vector (16 bytes).

    Returns:
        The base64-encoded ciphertext (string).
        Raises ValueError if key or IV are not the correct length.
    """

    if len(key) != 16 or len(iv) != 16:
        raise ValueError("Key and IV must be 16 bytes long for AES-128")

    if isinstance(message, str):
        message = message.encode('utf-8')  # Encode to bytes if needed

    cipher = AES.new(key, AES.MODE_CBC, iv)
    padded_message = pad(message, AES.block_size) # Apply PKCS7 padding
    ciphertext = cipher.encrypt(padded_message)
    return base64.b64encode(ciphertext).decode('utf-8')



iv = bytes.fromhex("7bde5a0f3f39fd658efc45de143cbc94")
password = bytes.fromhex("3e83b13d99bf0de6c6bde5ac5ca4ae68") 
msg = "this is a message"

out = encrypt_aes_cbc(msg, password, iv)

print(f"IV: {iv.hex()}")
print(f"PWD: {password.hex()}")
print(f"MSG: {msg}")
print(f"OUT: {out}")