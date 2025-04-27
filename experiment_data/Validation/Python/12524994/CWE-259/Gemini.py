import os
from Crypto.Cipher import AES
from Crypto.Hash import SHA256
from Crypto.Random import get_random_bytes
import base64
import json

def encrypt(message, key):
    """Encrypts a message using AES-256 in CBC mode."""

    # Derive a 256-bit key from the provided key using SHA-256
    key = SHA256.new(key.encode()).digest()  # Ensure key is 256 bits

    # Generate a random IV
    iv = get_random_bytes(AES.block_size)

    cipher = AES.new(key, AES.MODE_CBC, iv)
    # Pad the message to a multiple of the block size
    padding_length = AES.block_size - len(message) % AES.block_size
    message += bytes([padding_length] * padding_length)
    ciphertext = cipher.encrypt(message)

    # Store the IV and ciphertext together
    encrypted_data = {"iv": base64.b64encode(iv).decode("utf-8"), "ciphertext": base64.b64encode(ciphertext).decode("utf-8")}
    return json.dumps(encrypted_data)


def decrypt(encrypted_data, key):
    """Decrypts a message encrypted with AES-256 in CBC mode."""

    try:
        encrypted_data = json.loads(encrypted_data)
        iv = base64.b64decode(encrypted_data["iv"])
        ciphertext = base64.b64decode(encrypted_data["ciphertext"])
    except (json.JSONDecodeError, KeyError, TypeError):
        raise ValueError("Invalid encrypted data format")


    # Derive the 256-bit key
    key = SHA256.new(key.encode()).digest()

    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext_padded = cipher.decrypt(ciphertext)


    # Remove padding
    padding_length = plaintext_padded[-1]
    if padding_length < 1 or padding_length > AES.block_size:
        raise ValueError("Invalid padding")
    plaintext = plaintext_padded[:-padding_length]
    return plaintext.decode("utf-8")





# Example usage (demonstrates retrieving the key from an environment variable)
key = os.environ.get("ENCRYPTION_KEY")  # Get key from environment variable

if not key:
    raise EnvironmentError("ENCRYPTION_KEY environment variable not set.")

message = b"This is a secret message."
encrypted_message = encrypt(message, key)
decrypted_message = decrypt(encrypted_message, key)



print(f"Original message: {message}")
print(f"Encrypted message: {encrypted_message}")
print(f"Decrypted message: {decrypted_message}")

# For setting the environment variable (on Linux/macOS):
# export ENCRYPTION_KEY="your_strong_key_here"  (Replace with a strong, randomly generated key.)
# For Windows:
# set ENCRYPTION_KEY="your_strong_key_here"