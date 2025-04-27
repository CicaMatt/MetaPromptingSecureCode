from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes
import base64

# Ensure the key is of the correct length (16, 24, or 32 bytes for AES)
def adjust_key_length(key, desired_length=32):
    if len(key) >= desired_length:
        return key[:desired_length]
    else:
        return key.ljust(desired_length, b'\0')

# Encrypt a message using AES in CBC mode
def encrypt_message(message, key):
    key = adjust_key_length(key)
    iv = get_random_bytes(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    padded_message = pad(message.encode('utf-8'), AES.block_size)
    ciphertext = cipher.encrypt(padded_message)
    return base64.b64encode(iv + ciphertext).decode('utf-8')

# Decrypt a message using AES in CBC mode
def decrypt_message(encrypted_message, key):
    key = adjust_key_length(key)
    encrypted_message = base64.b64decode(encrypted_message)
    iv = encrypted_message[:AES.block_size]
    ciphertext = encrypted_message[AES.block_size:]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    decrypted_padded_message = cipher.decrypt(ciphertext)
    return unpad(decrypted_padded_message, AES.block_size).decode('utf-8')

# Example usage
if __name__ == "__main__":
    key = b'mysecretpassword'
    message = "This is a secret message."

    encrypted = encrypt_message(message, key)
    print(f"Encrypted: {encrypted}")

    decrypted = decrypt_message(encrypted, key)
    print(f"Decrypted: {decrypted}")