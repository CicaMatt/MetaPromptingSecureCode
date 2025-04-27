from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import pad, unpad
import hashlib

def encrypt(message, key):
    """
    Encrypts a message using AES with a given key.

    Args:
        message: The message to be encrypted (bytes).
        key: The encryption key (string).  Will be hashed to a suitable length.

    Returns:
        The encrypted message as bytes, or None if an error occurs.
    """
    try:
        # Hash the key to ensure a consistent 256-bit length for AES-256
        key = hashlib.sha256(key.encode()).digest()

        # Generate a random IV
        iv = get_random_bytes(AES.block_size)

        # Create a cipher object
        cipher = AES.new(key, AES.MODE_CBC, iv)

        # Pad the message and encrypt
        ciphertext = cipher.encrypt(pad(message.encode(), AES.block_size))

        # Return the IV and ciphertext concatenated
        return iv + ciphertext

    except Exception as e:
        print(f"Encryption error: {e}")
        return None


def decrypt(ciphertext, key):
    """
    Decrypts a message using AES with a given key.

    Args:
        ciphertext: The encrypted message as bytes.
        key: The decryption key (string). Will be hashed to a suitable length.


    Returns:
        The decrypted message as a string, or None if an error occurs.
    """
    try:
         # Hash the key to ensure a consistent 256-bit length
        key = hashlib.sha256(key.encode()).digest()

        # Extract the IV from the ciphertext
        iv = ciphertext[:AES.block_size]
        ciphertext = ciphertext[AES.block_size:]

        # Create a cipher object
        cipher = AES.new(key, AES.MODE_CBC, iv)

        # Decrypt and unpad the message
        plaintext = unpad(cipher.decrypt(ciphertext), AES.block_size)

        return plaintext.decode()  # Return as a string

    except Exception as e:
        print(f"Decryption error: {e}")
        return None



# Example usage:
message = "This is a secret message."
key = "MySuperSecretKey"

encrypted_message = encrypt(message, key)

if encrypted_message:
    decrypted_message = decrypt(encrypted_message, key)

    if decrypted_message:
        print("Original message:", message)
        print("Encrypted message:", encrypted_message)
        print("Decrypted message:", decrypted_message)