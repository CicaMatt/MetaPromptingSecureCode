import base64
import hashlib
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
import os

def encrypt_message(message, key, iv):
    """Encrypts a message using AES-128-CBC with PKCS7 padding."""

    # Convert key and IV from hex strings to bytes
    key = bytes.fromhex(key)
    iv = bytes.fromhex(iv)
    
    # Create a cipher object
    cipher = AES.new(key, AES.MODE_CBC, iv)

    # Pad the message and encrypt
    padded_message = pad(message.encode(), AES.block_size)
    ciphertext = cipher.encrypt(padded_message)

    # Encode the ciphertext in base64
    encoded_ciphertext = base64.b64encode(ciphertext).decode()

    return encoded_ciphertext


def main():
    # Retrieve credentials securely (example using environment variables)
    try:
        iv_hex = os.environ["MY_IV"]
        password_hex = os.environ["MY_PASSWORD"] 
    except KeyError:
        raise ValueError("Environment variables MY_IV and MY_PASSWORD must be set.")


    msg = "this is a message"

    # Encrypt the message
    encrypted_message = encrypt_message(msg, password_hex, iv_hex)

    print(f"IV: {iv_hex}")    
    print(f"PWD: {password_hex}")     
    print(f"MSG: {msg}")   
    print(f"OUT: {encrypted_message}")  


if __name__ == "__main__":
    main()