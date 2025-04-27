from Crypto import Random
from Crypto.Cipher import AES
import os

def encrypt(message, key=None, key_size=256):
    """
    Encrypts a message using AES-256 in CBC mode.
    
    :param message: The plaintext message to encrypt.
    :param key: The encryption key. If None, a random key is generated.
    :param key_size: The size of the key in bits.
    :return: A tuple containing the IV and the encrypted message.
    """
    def pad(s):
        """Pad the message to be a multiple of AES block size."""
        x = AES.block_size - len(s) % AES.block_size
        return s + (bytes([x]) * x)

    padded_message = pad(message)

    if key is None:
        key = Random.new().read(key_size // 8)

    iv = Random.new().read(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)

    return iv + cipher.encrypt(padded_message)

def decrypt(ciphertext, key):
    """
    Decrypts a message using AES-256 in CBC mode.
    
    :param ciphertext: The encrypted message.
    :param key: The decryption key.
    :return: The decrypted plaintext message.
    """
    unpad = lambda s: s[:-s[-1]]
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = unpad(cipher.decrypt(ciphertext[AES.block_size:]))

    return plaintext

def encrypt_file(file_name, key):
    """
    Encrypts the contents of a file and writes the encrypted data back to the file.
    
    :param file_name: The name of the file to encrypt.
    :param key: The encryption key.
    """
    try:
        with open(file_name, 'rb') as f:
            plaintext = f.read()

        enc = encrypt(plaintext, key)

        with open(file_name, 'wb') as f:
            f.write(enc)
    except Exception as e:
        print(f"Error encrypting file: {e}")
        raise

def decrypt_file(file_name, key):
    """
    Decrypts the contents of a file and writes the decrypted data back to the file.
    
    :param file_name: The name of the file to decrypt.
    :param key: The decryption key.
    """
    try:
        with open(file_name, 'rb') as f:
            ciphertext = f.read()

        dec = decrypt(ciphertext, key)

        with open(file_name, 'wb') as f:
            f.write(dec)
    except Exception as e:
        print(f"Error decrypting file: {e}")
        raise

# Example usage
if __name__ == "__main__":
    # Load the key from an environment variable for better security
    key = os.getenv('ENCRYPTION_KEY')
    if not key:
        raise ValueError("Encryption key not found in environment variables")

    # Ensure the key is in bytes format
    key = key.encode('utf-8')

    # Encrypt and decrypt a file
    file_name = 'to_enc.txt'
    encrypt_file(file_name, key)
    decrypt_file(file_name, key)