from Crypto.Cipher import AES
from Crypto.Hash import SHA256
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes
import base64

def encrypt_message(message, key):
    # Hash the key to ensure it is 32 bytes (AES-256)
    key_hash = SHA256.new(key.encode()).digest()
    
    # Generate a random 16-byte IV
    iv = get_random_bytes(16)
    
    # Create AES cipher in CBC mode
    cipher = AES.new(key_hash, AES.MODE_CBC, iv)
    
    # Pad the message to be a multiple of 16 bytes
    padded_message = pad(message.encode(), AES.block_size)
    
    # Encrypt the message
    ciphertext = cipher.encrypt(padded_message)
    
    # Combine IV and ciphertext for storage/transmission
    encrypted_message = base64.b64encode(iv + ciphertext).decode()
    
    return encrypted_message

def decrypt_message(encrypted_message, key):
    # Hash the key to ensure it is 32 bytes (AES-256)
    key_hash = SHA256.new(key.encode()).digest()
    
    # Decode the base64 encoded message
    decoded_message = base64.b64decode(encrypted_message)
    
    # Extract the IV (first 16 bytes) and the ciphertext
    iv = decoded_message[:16]
    ciphertext = decoded_message[16:]
    
    # Create AES cipher in CBC mode
    cipher = AES.new(key_hash, AES.MODE_CBC, iv)
    
    # Decrypt the ciphertext
    padded_message = cipher.decrypt(ciphertext)
    
    # Unpad the message
    message = unpad(padded_message, AES.block_size).decode()
    
    return message

# Example usage
key = "my_secret_key"
message = "This is a secret message."

encrypted = encrypt_message(message, key)
print(f"Encrypted: {encrypted}")

decrypted = decrypt_message(encrypted, key)
print(f"Decrypted: {decrypted}")