from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
import os
import base64

def encrypt_message(message: str, password: str) -> (str, str):
    """
    Encrypts a message with the provided password using AES in GCM mode.

    :param message: The message to encrypt.
    :param password: The password to use for key derivation.
    :return: A tuple of (encrypted message, nonce).
    """
    # Derive a key from the password
    password = password.encode('utf-8')
    salt = os.urandom(16)
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
        backend=default_backend()
    )
    key = base64.urlsafe_b64encode(kdf.derive(password))

    # Encrypt the message
    iv = os.urandom(12)  # Recommended length for GCM nonce
    encryptor = Cipher(
        algorithms.AES(key),
        modes.GCM(iv),
        backend=default_backend()
    ).encryptor()

    encryptor.authenticate_additional_data(salt)  # Ensure integrity of the salt
    ciphertext = encryptor.update(message.encode('utf-8')) + encryptor.finalize()

    return (base64.urlsafe_b64encode(ciphertext + encryptor.tag).decode('utf-8'),
            base64.urlsafe_b64encode(iv + salt).decode('utf-8'))

def decrypt_message(encrypted_message: str, nonce: str, password: str) -> str:
    """
    Decrypts a message with the provided password using AES in GCM mode.

    :param encrypted_message: The encrypted message to decrypt (base64 encoded).
    :param nonce: The nonce used for encryption (base64 encoded).
    :param password: The password used for key derivation.
    :return: The decrypted message.
    """
    encrypted_message = base64.urlsafe_b64decode(encrypted_message)
    nonce = base64.urlsafe_b64decode(nonce)

    iv = nonce[:12]
    salt = nonce[12:]

    # Derive the same key from the password
    password = password.encode('utf-8')
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=100000,
        backend=default_backend()
    )
    key = base64.urlsafe_b64encode(kdf.derive(password))

    # Decrypt the message
    tag = encrypted_message[-16:]
    ciphertext = encrypted_message[:-16]

    decryptor = Cipher(
        algorithms.AES(key),
        modes.GCM(iv, tag),
        backend=default_backend()
    ).decryptor()

    decryptor.authenticate_additional_data(salt)
    plaintext = decryptor.update(ciphertext) + decryptor.finalize()

    return plaintext.decode('utf-8')

# Example Usage
message_to_encrypt = "This is a secret message."
password_for_key = "SuperSecretPassword123"

encrypted_msg, nonce = encrypt_message(message_to_encrypt, password_for_key)
print("Encrypted Message:", encrypted_msg)
print("Nonce:", nonce)

decrypted_msg = decrypt_message(encrypted_msg, nonce, password_for_key)
print("Decrypted Message:", decrypted_msg)