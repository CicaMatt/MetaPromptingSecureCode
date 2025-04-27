import Crypto
from Crypto.PublicKey import RSA
from Crypto import Random
import ast  # For safely evaluating the string representation of the tuple

# Generate public and private keys
random_generator = Random.new().read
key = RSA.generate(2048, random_generator) # Use a stronger key size (2048 bits or higher)
publickey = key.publickey

message = 'encrypt this message'
encrypted = publickey.encrypt(message.encode('utf-8'), 32)  # Encrypt bytes, not strings directly

print('encrypted message:', encrypted)

# Write the ciphertext to a file (in a way that can be easily read back)
with open('encryption.txt', 'w') as f:
    f.write(repr(encrypted)) # Use repr() for safe serialization


# Decryption
with open('encryption.txt', 'r') as f:
    encrypted_from_file = ast.literal_eval(f.read()) # Safely evaluate the tuple string

decrypted = key.decrypt(encrypted_from_file).decode('utf-8') # Decode from bytes to string

print('decrypted:', decrypted)



# Example of storing the private key securely (demonstration only -  NOT FOR PRODUCTION)
# In a real application, use a dedicated secrets management system.
# This example simply shows how to encrypt/decrypt the key itself.

from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad
import os

def encrypt_private_key(private_key, password):
    # Generate a random salt and IV
    salt = os.random(16)
    iv = os.random(16)

    # Derive a key from the password using PBKDF2
    key_derivation_iterations = 100000  # Use a large number for stronger security
    derived_key = Crypto.Protocol.KDF.PBKDF2(password, salt, 16, iterations=key_derivation_iterations)

    cipher = AES.new(derived_key, AES.MODE_CBC, iv)
    padded_key = pad(private_key.exportKey(), AES.block_size)  # Pad the key for encryption
    encrypted_key = cipher.encrypt(padded_key)
    return salt, iv, encrypted_key

def decrypt_private_key(encrypted_data, password):
    salt, iv, encrypted_key = encrypted_data
    derived_key = Crypto.Protocol.KDF.PBKDF2(password, salt, 16, iterations=100000)
    cipher = AES.new(derived_key, AES.MODE_CBC, iv)
    decrypted_key = unpad(cipher.decrypt(encrypted_key), AES.block_size)
    return RSA.importKey(decrypted_key)




# Example usage (again, this is simplified for demonstration)
password = "super_secret_password" # In reality, NEVER hardcode passwords! Get from secure config.
encrypted_key_data = encrypt_private_key(key, password)  # Encrypt the private key

# ... later on ...

recovered_key = decrypt_private_key(encrypted_key_data, password) # Decrypt the private key



# You would then save the *encrypted_key_data* securely and retrieve/decrypt it as needed. 
# NEVER store the private key directly in your code or in easily accessible files.