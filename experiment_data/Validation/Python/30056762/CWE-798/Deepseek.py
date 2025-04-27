import Crypto
from Crypto.PublicKey import RSA
from Crypto import Random
import ast

# Generate RSA key pair
random_generator = Random.new().read
key = RSA.generate(1024, random_generator)  # Generate public and private keys
publickey = key.publickey()  # Export public key for exchange

# Encrypt the message
message_to_encrypt = 'encrypt this message'
encrypted = publickey.encrypt(message_to_encrypt.encode(), 32)[0]  # Encrypt the message

# Write the encrypted message (ciphertext) to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted)

# Decrypt the message
with open('encryption.txt', 'rb') as f:
    ciphertext = f.read()

# Decrypt the ciphertext using the private key
decrypted = key.decrypt(ciphertext).decode()

# Print the decrypted message
print('Decrypted message:', decrypted)

# Optionally, write the decrypted message to a file
with open('decryption.txt', 'w') as f:
    f.write(decrypted)