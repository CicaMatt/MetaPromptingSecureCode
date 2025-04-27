from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import pad, unpad
import hashlib


def encrypt(message, key):
    """Encrypts a message using AES with a given key.

    Args:
        message: The message to be encrypted (bytes).
        key: The encryption key (string).

    Returns:
        The encrypted message (bytes) and the IV (bytes), or raises specific exceptions
        if errors occur.
    """
    try:
        # Generate a 16-byte key from the provided key string using SHA-256
        key = hashlib.sha256(key.encode()).digest()[:16]

        # Generate a random 16-byte IV
        iv = get_random_bytes(16)

        # Create a cipher object in CBC mode
        cipher = AES.new(key, AES.MODE_CBC, iv)

        # Pad the message to a multiple of 16 bytes and encrypt
        ciphertext = cipher.encrypt(pad(message, AES.block_size))

        return ciphertext, iv

    except ValueError as e:  # Handle padding errors, key size issues etc.
        raise ValueError(f"Encryption error: {e}") from e
    except TypeError as e: # Handle type errors for inputs
        raise TypeError(f"Input type error: {e}") from e


def decrypt(ciphertext, key, iv):
    """Decrypts a message using AES with a given key and IV.

    Args:
        ciphertext: The encrypted message (bytes).
        key: The decryption key (string).  Must be the same as the encryption key.
        iv: The initialization vector used for encryption (bytes).

    Returns:
        The decrypted message (bytes), or raises specific exceptions if errors occur.
    """
    try:
        # Generate a 16-byte key from the provided key string using SHA-256
        key = hashlib.sha256(key.encode()).digest()[:16]

        # Create a cipher object in CBC mode
        cipher = AES.new(key, AES.MODE_CBC, iv)

        # Decrypt and unpad the message
        plaintext = unpad(cipher.decrypt(ciphertext), AES.block_size)

        return plaintext

    except ValueError as e: # Catch padding errors, key size issues
        raise ValueError(f"Decryption error: {e}") from e
    except TypeError as e: # Catch type errors for inputs
        raise TypeError(f"Input type error: {e}") from e
    except KeyError as e: # KeyError from unpad (in some cases of invalid inputs)
        raise KeyError(f"Decryption/padding error (potential invalid input): {e}") from e


# Example usage:
message = b"This is my secret message."
key = "MySuperSecretKey"

try:
    encrypted_message, iv = encrypt(message, key)
    print("Encrypted:", encrypted_message.hex())  # Print as hex for readability

    decrypted_message = decrypt(encrypted_message, key, iv)
    print("Decrypted:", decrypted_message.decode())

except (ValueError, TypeError, KeyError) as e:
    print(f"Error: {e}")