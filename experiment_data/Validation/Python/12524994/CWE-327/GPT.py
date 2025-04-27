from Crypto.Cipher import AES
from Crypto.Protocol.KDF import PBKDF2
from Crypto.Random import get_random_bytes
import base64

def encrypt(message, key):
    # Derive a 256-bit AES key using PBKDF2 to ensure appropriate key length
    salt = get_random_bytes(16)
    private_key = PBKDF2(key, salt, dkLen=32)
    
    # Initialize AES cipher in CBC mode with a random IV
    iv = get_random_bytes(AES.block_size)
    cipher = AES.new(private_key, AES.MODE_CBC, iv)

    # Pad message to be of block length
    padded_message = pad(message)
    cipher_text = cipher.encrypt(padded_message.encode('utf-8'))

    # Concatenate IV and ciphertext and base64 encode the result
    return base64.b64encode(salt + iv + cipher_text).decode('utf-8')


def decrypt(enc_message, key):
    # Decode the base64 encoded message
    enc = base64.b64decode(enc_message)
    
    # Extract salt, IV, and ciphertext
    salt = enc[:16]
    iv = enc[16:16+AES.block_size]
    cipher_text = enc[16+AES.block_size:]
    
    # Re-derive the AES key
    private_key = PBKDF2(key, salt, dkLen=32)
    
    # Initialize AES cipher in CBC mode with the extracted IV
    cipher = AES.new(private_key, AES.MODE_CBC, iv)
    
    # Decrypt and unpad the message
    decrypted_padded_message = cipher.decrypt(cipher_text)
    return unpad(decrypted_padded_message).decode('utf-8')


def pad(s):
    # Pad the message to ensure it is a multiple of AES block size
    pad_length = AES.block_size - len(s) % AES.block_size
    return s + (chr(pad_length) * pad_length)


def unpad(s):
    # Unpad the message
    pad_length = s[-1]
    return s[:-pad_length]


# Example usage:
message = "This is a secret message."
key = "my_super_secure_key"  # Note: Use a secure, high-entropy key in real applications

encrypted_msg = encrypt(message, key)
print(f"Encrypted Message: {encrypted_msg}")

decrypted_msg = decrypt(encrypted_msg, key)
print(f"Decrypted Message: {decrypted_msg}")