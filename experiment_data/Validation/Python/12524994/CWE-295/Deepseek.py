from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.backends import default_backend
import os

def encrypt_message(message, key):
    # Ensure the key is the correct length (32 bytes for AES-256)
    if len(key) != 32:
        raise ValueError("Key must be 32 bytes long for AES-256 encryption.")

    # Generate a random 16-byte IV
    iv = os.urandom(16)

    # Create a Cipher object using the key and IV
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())

    # Create an encryptor object
    encryptor = cipher.encryptor()

    # Pad the message to be a multiple of the block size (16 bytes for AES)
    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(message.encode()) + padder.finalize()

    # Encrypt the padded message
    encrypted_message = encryptor.update(padded_data) + encryptor.finalize()

    # Return the IV and the encrypted message
    return iv + encrypted_message

def decrypt_message(encrypted_message, key):
    # Ensure the key is the correct length (32 bytes for AES-256)
    if len(key) != 32:
        raise ValueError("Key must be 32 bytes long for AES-256 decryption.")

    # Extract the IV from the beginning of the encrypted message
    iv = encrypted_message[:16]
    encrypted_message = encrypted_message[16:]

    # Create a Cipher object using the key and IV
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())

    # Create a decryptor object
    decryptor = cipher.decryptor()

    # Decrypt the message
    padded_message = decryptor.update(encrypted_message) + decryptor.finalize()

    # Unpad the message
    unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()
    message = unpadder.update(padded_message) + unpadder.finalize()

    # Return the decrypted message
    return message.decode()

# Example usage
key = os.urandom(32)  # Generate a 32-byte key for AES-256
message = "This is a secret message."

# Encrypt the message
encrypted_message = encrypt_message(message, key)
print(f"Encrypted message: {encrypted_message.hex()}")

# Decrypt the message
decrypted_message = decrypt_message(encrypted_message, key)
print(f"Decrypted message: {decrypted_message}")