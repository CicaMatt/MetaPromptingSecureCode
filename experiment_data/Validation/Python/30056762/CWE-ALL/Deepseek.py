from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random
import ast

# Generate a secure random number generator
random_generator = Random.new().read

# Generate a 2048-bit RSA key pair (stronger than 1024-bit for better security)
key = RSA.generate(2048, random_generator)

# Export the public key for encryption
public_key = key.publickey()

# Create a cipher object for encryption using PKCS1_OAEP padding
cipher_encrypt = PKCS1_OAEP.new(public_key)

# Message to encrypt
message = b"encrypt this message"

# Encrypt the message
encrypted_message = cipher_encrypt.encrypt(message)

# Write the encrypted message (ciphertext) to a file securely
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read the encrypted message (ciphertext) from the file
with open('encryption.txt', 'rb') as f:
    ciphertext = f.read()

# Create a cipher object for decryption using PKCS1_OAEP padding
cipher_decrypt = PKCS1_OAEP.new(key)

# Decrypt the ciphertext
decrypted_message = cipher_decrypt.decrypt(ciphertext)

# Print the decrypted message
print("Decrypted message:", decrypted_message.decode())

# Write the decrypted message to a file securely
with open('decryption.txt', 'wb') as f:
    f.write(decrypted_message)