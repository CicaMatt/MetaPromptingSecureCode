from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import pad, unpad
import hashlib

def encrypt(message, key):
    """
    Encrypts a message using AES-256 in CBC mode.

    Args:
        message: The message to encrypt (bytes).
        key: The encryption key (string or bytes).

    Returns:
        A tuple containing the IV and ciphertext (both bytes).
        Returns None if an error occurs.
    """
    try:
        # Generate a 256-bit key from the provided key (if needed)
        key = hashlib.sha256(key.encode() if isinstance(key, str) else key).digest()  # Ensure 256-bit key

        # Generate a random IV
        iv = get_random_bytes(AES.block_size)

        # Create the cipher object
        cipher = AES.new(key, AES.MODE_CBC, iv)

        # Pad the message to a multiple of the block size
        padded_message = pad(message, AES.block_size)

        # Encrypt the message
        ciphertext = cipher.encrypt(padded_message)

        return iv, ciphertext

    except Exception as e:  # Handle potential exceptions (e.g., invalid key)
        print(f"Encryption error: {e}")
        return None



def decrypt(ciphertext, key, iv):
    """
    Decrypts a message using AES-256 in CBC mode.

    Args:
        ciphertext: The ciphertext to decrypt (bytes).
        key: The decryption key (string or bytes).
        iv: The initialization vector (bytes).

    Returns:
        The decrypted message (bytes) or None if decryption fails.
    """

    try:
        # Generate a 256-bit key from the provided key (if needed)
        key = hashlib.sha256(key.encode() if isinstance(key, str) else key).digest()

        # Create the cipher object
        cipher = AES.new(key, AES.MODE_CBC, iv)
        
        # Decrypt the ciphertext
        plaintext_padded = cipher.decrypt(ciphertext)

        # Unpad the message
        plaintext = unpad(plaintext_padded, AES.block_size)

        return plaintext

    except Exception as e: # Handle potential exceptions (e.g., padding errors, incorrect key/IV)
        print(f"Decryption error: {e}")
        return None




# Example usage:
message = b"This is my secret message."
key = "mysecretpassword"


encryption_result = encrypt(message, key)
if encryption_result:
    iv, ciphertext = encryption_result
    print("Ciphertext:", ciphertext.hex())  # Print ciphertext in hexadecimal for easier handling/storage


    decrypted_message = decrypt(ciphertext, key, iv)
    if decrypted_message:
        print("Decrypted message:", decrypted_message)