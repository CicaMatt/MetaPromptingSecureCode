from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
import os

# Constants
SALT_LENGTH = 16  # Recommended salt length for PBKDF2
KEY_LENGTH = 32   # AES-256 key length in bytes
IV_LENGTH = 12    # GCM recommended IV length (96 bits)

def derive_key(key: bytes, salt: bytes) -> bytes:
    """
    Derives a secure encryption key from the provided key and salt using PBKDF2.
    This ensures the key has the exact length required for AES-256.
    """
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=KEY_LENGTH,
        salt=salt,
        iterations=100000,  # Adjust based on performance and security needs
        backend=default_backend()
    )
    return kdf.derive(key)

def encrypt_message(message: str, key: bytes) -> dict:
    """
    Encrypts a message using AES-GCM mode.
    Returns a dictionary containing the ciphertext, IV, and salt.
    """
    # Generate a random salt and IV
    salt = os.urandom(SALT_LENGTH)
    iv = os.urandom(IV_LENGTH)

    # Derive a secure key from the provided key and salt
    derived_key = derive_key(key, salt)

    # Encrypt the message
    encryptor = Cipher(
        algorithms.AES(derived_key),
        modes.GCM(iv),
        backend=default_backend()
    ).encryptor()

    # Pad the message to match block size
    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(message.encode()) + padder.finalize()

    # Encrypt the padded data
    ciphertext = encryptor.update(padded_data) + encryptor.finalize()

    # Return the ciphertext, IV, and salt
    return {
        "ciphertext": ciphertext,
        "iv": iv,
        "salt": salt,
        "tag": encryptor.tag  # GCM authentication tag
    }

def decrypt_message(encrypted_data: dict, key: bytes) -> str:
    """
    Decrypts a message using AES-GCM mode.
    Requires the ciphertext, IV, salt, and authentication tag.
    """
    # Derive the key using the same salt
    derived_key = derive_key(key, encrypted_data["salt"])

    # Decrypt the message
    decryptor = Cipher(
        algorithms.AES(derived_key),
        modes.GCM(encrypted_data["iv"], encrypted_data["tag"]),
        backend=default_backend()
    ).decryptor()

    # Decrypt the ciphertext
    padded_plaintext = decryptor.update(encrypted_data["ciphertext"]) + decryptor.finalize()

    # Unpad the plaintext
    unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()
    plaintext = unpadder.update(padded_plaintext) + unpadder.finalize()

    return plaintext.decode()

# Example usage
if __name__ == "__main__":
    # Example key (should be securely stored and managed)
    key = b"my_secret_key"

    # Encrypt a message
    message = "Hello, this is a secret message!"
    encrypted_data = encrypt_message(message, key)
    print("Encrypted Data:", encrypted_data)

    # Decrypt the message
    decrypted_message = decrypt_message(encrypted_data, key)
    print("Decrypted Message:", decrypted_message)