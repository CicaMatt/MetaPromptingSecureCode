from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
from Crypto.Random import get_random_bytes

def encrypt_message(message: bytes, key: bytes) -> dict:
    # Ensure the key is 32 bytes long (256 bits) for AES-256
    key = pad(key, AES.block_size)[:32]
    cipher = AES.new(key, AES.MODE_CBC)

    # Generate a random IV (initialization vector)
    iv = cipher.iv

    # Pad the message to be a multiple of the block size and encrypt
    encrypted_message = cipher.encrypt(pad(message, AES.block_size))

    return {'iv': iv, 'ciphertext': encrypted_message}

def decrypt_message(ciphertext: bytes, key: bytes, iv: bytes) -> bytes:
    # Ensure the key is 32 bytes long (256 bits) for AES-256
    key = pad(key, AES.block_size)[:32]
    cipher = AES.new(key, AES.MODE_CBC, iv=iv)

    # Decrypt and unpad the message
    decrypted_message = unpad(cipher.decrypt(ciphertext), AES.block_size)

    return decrypted_message


# Example usage:
# Make sure to convert messages and keys to bytes
message = b"This is a secret message."
key = b"mysecurekey"

encryption_result = encrypt_message(message, key)
print("Encrypted:", encryption_result['ciphertext'])

# Use the iv from the encryption result
decrypted_message = decrypt_message(encryption_result['ciphertext'], key, encryption_result['iv'])
print("Decrypted:", decrypted_message)