from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes
import base64

# Constants
BLOCK_SIZE = 16  # AES block size is 16 bytes

def encrypt_message(message, key):
    # Ensure key is the correct length
    key = pad(key.encode(), BLOCK_SIZE)[:BLOCK_SIZE]

    # Generate a random IV
    iv = get_random_bytes(BLOCK_SIZE)

    # Create AES cipher
    cipher = AES.new(key, AES.MODE_CBC, iv)

    # Pad the message to fit the block size
    padded_message = pad(message.encode(), BLOCK_SIZE)

    # Encrypt the message
    encrypted_message = cipher.encrypt(padded_message)

    # Return as Base64 to ensure the output is easily storable
    return base64.b64encode(iv + encrypted_message).decode()

def decrypt_message(encrypted_message, key):
    # Ensure key is the correct length
    key = pad(key.encode(), BLOCK_SIZE)[:BLOCK_SIZE]

    # Decode from Base64
    encrypted_message_bytes = base64.b64decode(encrypted_message)

    # Extract the IV and encrypted message
    iv = encrypted_message_bytes[:BLOCK_SIZE]
    encrypted_message = encrypted_message_bytes[BLOCK_SIZE:]

    # Create AES cipher
    cipher = AES.new(key, AES.MODE_CBC, iv)

    # Decrypt the message and unpad it
    decrypted_message = unpad(cipher.decrypt(encrypted_message), BLOCK_SIZE)

    # Return as decoded string
    return decrypted_message.decode()

# Example usage:
message = "This is a secret message"
key = "mysecretpassword"
encrypted = encrypt_message(message, key)
print(f"Encrypted: {encrypted}")
decrypted = decrypt_message(encrypted, key)
print(f"Decrypted: {decrypted}")