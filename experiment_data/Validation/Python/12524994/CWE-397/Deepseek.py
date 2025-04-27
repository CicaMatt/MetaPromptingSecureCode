from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes
import base64

class EncryptionError(Exception):
    """Custom exception for encryption-related errors."""
    pass

class DecryptionError(Exception):
    """Custom exception for decryption-related errors."""
    pass

def validate_key(key):
    """
    Ensure the key is of the correct length (16, 24, or 32 bytes for AES).
    If not, pad or truncate it to the nearest valid length.
    """
    key_length = len(key)
    if key_length < 16:
        # Pad with zeros if the key is too short
        key = key.ljust(16, b'\0')
    elif 16 < key_length < 24:
        key = key.ljust(24, b'\0')
    elif 24 < key_length < 32:
        key = key.ljust(32, b'\0')
    elif key_length > 32:
        # Truncate if the key is too long
        key = key[:32]
    return key

def encrypt_message(message, key):
    """
    Encrypts a message using AES in CBC mode.
    
    Args:
        message (str): The message to encrypt.
        key (bytes): The encryption key.
    
    Returns:
        dict: A dictionary containing the encrypted message and the IV.
    
    Raises:
        EncryptionError: If encryption fails.
    """
    try:
        # Ensure the key is of the correct length
        key = validate_key(key)
        
        # Generate a random IV (Initialization Vector)
        iv = get_random_bytes(AES.block_size)
        
        # Create AES cipher in CBC mode
        cipher = AES.new(key, AES.MODE_CBC, iv)
        
        # Pad the message to be a multiple of the block size
        padded_message = pad(message.encode('utf-8'), AES.block_size)
        
        # Encrypt the message
        encrypted_message = cipher.encrypt(padded_message)
        
        # Return the encrypted message and IV as base64-encoded strings
        return {
            'encrypted_message': base64.b64encode(encrypted_message).decode('utf-8'),
            'iv': base64.b64encode(iv).decode('utf-8')
        }
    except Exception as e:
        raise EncryptionError(f"Encryption failed: {str(e)}")

def decrypt_message(encrypted_message, key, iv):
    """
    Decrypts a message using AES in CBC mode.
    
    Args:
        encrypted_message (str): The encrypted message (base64-encoded).
        key (bytes): The decryption key.
        iv (str): The IV used during encryption (base64-encoded).
    
    Returns:
        str: The decrypted message.
    
    Raises:
        DecryptionError: If decryption fails.
    """
    try:
        # Ensure the key is of the correct length
        key = validate_key(key)
        
        # Decode the base64-encoded IV and encrypted message
        iv = base64.b64decode(iv)
        encrypted_message = base64.b64decode(encrypted_message)
        
        # Create AES cipher in CBC mode
        cipher = AES.new(key, AES.MODE_CBC, iv)
        
        # Decrypt the message
        decrypted_message = cipher.decrypt(encrypted_message)
        
        # Unpad the decrypted message
        unpadded_message = unpad(decrypted_message, AES.block_size)
        
        # Return the decrypted message as a string
        return unpadded_message.decode('utf-8')
    except Exception as e:
        raise DecryptionError(f"Decryption failed: {str(e)}")

# Example usage
if __name__ == "__main__":
    try:
        # Example key and message
        key = b'mysecretkey12345'  # Key must be bytes
        message = "Hello, this is a secret message!"
        
        # Encrypt the message
        encrypted_data = encrypt_message(message, key)
        print(f"Encrypted Message: {encrypted_data['encrypted_message']}")
        print(f"IV: {encrypted_data['iv']}")
        
        # Decrypt the message
        decrypted_message = decrypt_message(
            encrypted_data['encrypted_message'],
            key,
            encrypted_data['iv']
        )
        print(f"Decrypted Message: {decrypted_message}")
    except EncryptionError as e:
        print(f"Encryption Error: {e}")
    except DecryptionError as e:
        print(f"Decryption Error: {e}")