import os
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from hashlib import sha256
from base64 import b64encode, b64decode

# Key derivation function (KDF) to ensure proper key length and strength
def derive_key(password, salt=None):
    if salt is None:
        salt = os.urandom(16)  # Generate a random salt if not provided
    key = sha256(password + salt).digest()  # Use SHA-256 for key derivation
    return key, salt


def encrypt(message, password):
    key, salt = derive_key(password)  # Derive key from password
    cipher = AES.new(key, AES.MODE_CBC)  # Use CBC mode with a randomly generated IV
    iv = cipher.iv
    ciphertext = cipher.encrypt(pad(message.encode('utf-8'), AES.block_size))
    return b64encode(salt + iv + ciphertext).decode('utf-8')  # Encode everything for safe storage/transmission


def decrypt(ciphertext, password):
    try:
        decoded_ciphertext = b64decode(ciphertext)
        salt = decoded_ciphertext[:16]  # Extract salt
        iv = decoded_ciphertext[16:32]  # Extract IV
        ciphertext = decoded_ciphertext[32:]  # Extract actual ciphertext
        key = derive_key(password, salt)[0]  # Derive the key using the same salt
        cipher = AES.new(key, AES.MODE_CBC, iv)  # Use the same IV for decryption
        plaintext = unpad(cipher.decrypt(ciphertext), AES.block_size).decode('utf-8')
        return plaintext
    except (ValueError, KeyError): # Handle specific exceptions related to decryption failures
        return None # Indicate decryption failure (better to raise a custom exception here)



# Example Usage (replace with secure password management):
password = b"MySuperSecurePassword"  # In a real application, store this securely!
message = "This is a secret message."

encrypted_message = encrypt(message, password)
decrypted_message = decrypt(encrypted_message, password)


if decrypted_message:
    print("Decrypted:", decrypted_message)
else:
    print("Decryption failed. Incorrect password or corrupted data.")