from Crypto.Cipher import AES
from Crypto.Protocol.KDF import PBKDF2
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes
import base64

# Constants
SALT_SIZE = 16  # Salt size for PBKDF2
KEY_SIZE = 32   # AES-256 key size
ITERATIONS = 100000  # Number of iterations for PBKDF2

def derive_key(password, salt):
    """Derive a key from a password using PBKDF2."""
    return PBKDF2(password, salt, dkLen=KEY_SIZE, count=ITERATIONS)

def encrypt_message(message, password):
    """Encrypt a message using AES-256 in CBC mode."""
    # Generate a random salt and IV
    salt = get_random_bytes(SALT_SIZE)
    iv = get_random_bytes(AES.block_size)
    
    # Derive the key from the password and salt
    key = derive_key(password, salt)
    
    # Create AES cipher in CBC mode
    cipher = AES.new(key, AES.MODE_CBC, iv)
    
    # Pad the message and encrypt
    padded_message = pad(message.encode('utf-8'), AES.block_size)
    ciphertext = cipher.encrypt(padded_message)
    
    # Combine salt, IV, and ciphertext for storage
    encrypted_data = salt + iv + ciphertext
    
    # Return base64-encoded result for easy storage/transmission
    return base64.b64encode(encrypted_data).decode('utf-8')

def decrypt_message(encrypted_message, password):
    """Decrypt a message using AES-256 in CBC mode."""
    # Decode the base64-encoded message
    encrypted_data = base64.b64decode(encrypted_message)
    
    # Extract salt, IV, and ciphertext
    salt = encrypted_data[:SALT_SIZE]
    iv = encrypted_data[SALT_SIZE:SALT_SIZE + AES.block_size]
    ciphertext = encrypted_data[SALT_SIZE + AES.block_size:]
    
    # Derive the key from the password and salt
    key = derive_key(password, salt)
    
    # Create AES cipher in CBC mode
    cipher = AES.new(key, AES.MODE_CBC, iv)
    
    # Decrypt and unpad the message
    padded_message = cipher.decrypt(ciphertext)
    message = unpad(padded_message, AES.block_size)
    
    # Return the decrypted message as a string
    return message.decode('utf-8')

# Example Usage
if __name__ == "__main__":
    password = "my_secure_password"
    message = "This is a secret message."
    
    # Encrypt the message
    encrypted = encrypt_message(message, password)
    print(f"Encrypted: {encrypted}")
    
    # Decrypt the message
    decrypted = decrypt_message(encrypted, password)
    print(f"Decrypted: {decrypted}")