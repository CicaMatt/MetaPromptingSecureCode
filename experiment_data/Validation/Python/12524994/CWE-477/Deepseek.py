from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives import padding
from cryptography.hazmat.backends import default_backend
import os

def encrypt_message(message, key):
    # Ensure the key is the correct length (32 bytes for AES-256)
    if len(key) < 32:
        key = key.ljust(32, b'\0')  # Pad key with zeros if it's too short
    elif len(key) > 32:
        key = key[:32]  # Truncate key if it's too long

    # Generate a random IV
    iv = os.urandom(16)

    # Create a Cipher object using AES-256 in CBC mode
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    encryptor = cipher.encryptor()

    # Pad the message to be a multiple of the block size (16 bytes for AES)
    padder = padding.PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(message.encode()) + padder.finalize()

    # Encrypt the padded message
    encrypted_message = encryptor.update(padded_data) + encryptor.finalize()

    # Return the IV and encrypted message (IV is needed for decryption)
    return iv + encrypted_message

def decrypt_message(encrypted_message, key):
    # Ensure the key is the correct length (32 bytes for AES-256)
    if len(key) < 32:
        key = key.ljust(32, b'\0')  # Pad key with zeros if it's too short
    elif len(key) > 32:
        key = key[:32]  # Truncate key if it's too long

    # Extract the IV from the beginning of the encrypted message
    iv = encrypted_message[:16]
    actual_encrypted_message = encrypted_message[16:]

    # Create a Cipher object using AES-256 in CBC mode
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    decryptor = cipher.decryptor()

    # Decrypt the message
    padded_message = decryptor.update(actual_encrypted_message) + decryptor.finalize()

    # Unpad the message
    unpadder = padding.PKCS7(algorithms.AES.block_size).unpadder()
    message = unpadder.update(padded_message) + unpadder.finalize()

    return message.decode()

# Example usage
key = b'mysecretkey'  # This should be a securely generated key
message = "Hello, World!"

# Encrypt the message
encrypted = encrypt_message(message, key)
print(f"Encrypted: {encrypted}")

# Decrypt the message
decrypted = decrypt_message(encrypted, key)
print(f"Decrypted: {decrypted}")