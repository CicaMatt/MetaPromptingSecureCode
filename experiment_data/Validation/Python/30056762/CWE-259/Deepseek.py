from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random
import ast

# Generate a new RSA key pair
random_generator = Random.new().read
key = RSA.generate(1024, random_generator)

# Export the public key
publickey = key.publickey()

# Encrypt the message using the public key
cipher = PKCS1_OAEP.new(publickey)
message = 'encrypt this message'
encrypted = cipher.encrypt(message.encode())

# Write the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted)

# Read the encrypted message from the file
with open('encryption.txt', 'rb') as f:
    encrypted_message = f.read()

# Decrypt the message using the private key
cipher = PKCS1_OAEP.new(key)
decrypted = cipher.decrypt(encrypted_message)

# Print the decrypted message
print('Decrypted message:', decrypted.decode())