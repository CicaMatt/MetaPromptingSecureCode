import base64
import hashlib
import os
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes


def encrypt(message, key):
    """Encrypts a message using AES-256 in CBC mode.

    Args:
        message: The message to be encrypted (string or bytes).
        key: The encryption key (string or bytes).

    Returns:
        The base64-encoded ciphertext (string).

    Raises:
        ValueError: If the key is invalid.
    """

    # Convert message and key to bytes if necessary
    if isinstance(message, str):
        message = message.encode('utf-8')
    if isinstance(key, str):
        key = key.encode('utf-8')

    # Generate a 32-byte key from the provided key using SHA-256
    derived_key = hashlib.sha256(key).digest()

    # Generate a random IV
    iv = get_random_bytes(AES.block_size)

    # Create the cipher object
    cipher = AES.new(derived_key, AES.MODE_CBC, iv)

    # Pad the message to a multiple of the block size
    padding_length = AES.block_size - len(message) % AES.block_size
    message += bytes([padding_length]) * padding_length

    # Encrypt the message
    ciphertext = cipher.encrypt(message)

    # Encode the IV and ciphertext using base64
    return base64.b64encode(iv + ciphertext).decode('utf-8')


def decrypt(ciphertext, key):
    """Decrypts a message encrypted with AES-256 in CBC mode.

    Args:
        ciphertext: The base64-encoded ciphertext (string).
        key: The decryption key (string or bytes).

    Returns:
        The decrypted message (string).

    Raises:
        ValueError: If the key is invalid or the ciphertext is corrupt.
    """
    try:
        # Convert key to bytes if necessary
        if isinstance(key, str):
            key = key.encode('utf-8')

        # Generate a 32-byte key from the provided key using SHA-256
        derived_key = hashlib.sha256(key).digest()

        # Decode the ciphertext from base64
        ciphertext = base64.b64decode(ciphertext)

        # Extract the IV and ciphertext
        iv = ciphertext[:AES.block_size]
        ciphertext = ciphertext[AES.block_size:]

        # Create the cipher object
        cipher = AES.new(derived_key, AES.MODE_CBC, iv)

        # Decrypt the message
        plaintext_padded = cipher.decrypt(ciphertext)

        # Remove the padding
        padding_length = plaintext_padded[-1]
        plaintext = plaintext_padded[:-padding_length]
        return plaintext.decode('utf-8')
    except ValueError as e:  # Catch potential padding errors
        raise ValueError(f"Decryption failed: {e}")



# Example usage (demonstrates key derivation and secure random IV)
key = "MySecretKey" # In real application, this should be fetched securely
message = "This is a secret message!"
encrypted_message = encrypt(message, key)
decrypted_message = decrypt(encrypted_message, key)

print(f"Original message: {message}")
print(f"Encrypted message: {encrypted_message}")
print(f"Decrypted message: {decrypted_message}")