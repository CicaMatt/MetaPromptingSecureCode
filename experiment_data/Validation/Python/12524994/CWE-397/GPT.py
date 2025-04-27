from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
from base64 import b64encode, b64decode
import os

class EncryptionError(Exception):
    """Custom exception for encryption errors"""
    pass

class DecryptionError(Exception):
    """Custom exception for decryption errors"""
    pass

def derive_key(key, salt):
    """Derive a secure AES key using PBKDF2HMAC, ensure correct length"""
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,  # AES key length: 16, 24, 32 bytes
        salt=salt,
        iterations=100000,
        backend=default_backend()
    )
    return kdf.derive(key.encode())

def encrypt_message(message, key):
    try:
        salt = os.urandom(16)  # Securely generate a random salt
        derived_key = derive_key(key, salt)

        # Pad the message to ensure it's a multiple of block size
        padder = padding.PKCS7(algorithms.AES.block_size).padder()
        padded_data = padder.update(message.encode()) + padder.finalize()

        iv = os.urandom(16)  # Securely generate a random IV
        cipher = Cipher(algorithms.AES(derived_key), modes.CBC(iv), backend=default_backend())
        encryptor = cipher.encryptor()
        encrypted_message = encryptor.update(padded_data) + encryptor.finalize()

        # Include the salt and iv with the encrypted message for decryption
        return b64encode(salt + iv + encrypted_message).decode()
    except Exception as e:
        raise EncryptionError(f"Encryption failed: {str(e)}")

def decrypt_message(encrypted_message, key):
    try:
        encrypted_data = b64decode(encrypted_message)
        salt = encrypted_data[:16]
        iv = encrypted_data[16:32]
        ciphertext = encrypted_data[32:]

        derived_key = derive_key(key, salt)

        cipher = Cipher(algorithms.AES(derived_key), modes.CBC(iv), backend=default_backend())
        decryptor = cipher.decryptor()
        padded_data = decryptor.update(ciphertext) + decryptor.finalize()

        # Unpad the message
        unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()
        unpadded_message = unpadder.update(padded_data) + unpadder.finalize()

        return unpadded_message.decode()
    except Exception as e:
        raise DecryptionError(f"Decryption failed: {str(e)}")

# Example usage:
message = "Secret Message"
key = "SecurePassword"

try:
    encrypted_message = encrypt_message(message, key)
    print("Encrypted:", encrypted_message)

    decrypted_message = decrypt_message(encrypted_message, key)
    print("Decrypted:", decrypted_message)
except (EncryptionError, DecryptionError) as e:
    print(e)