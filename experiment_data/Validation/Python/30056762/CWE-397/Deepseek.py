from Crypto.PublicKey import RSA
from Crypto import Random
import ast

# Generate public and private keys
random_generator = Random.new().read
key = RSA.generate(1024, random_generator)
publickey = key.publickey()

# Encrypt the message
message = 'encrypt this message'
encrypted = publickey.encrypt(message.encode(), 32)

# Write the ciphertext to a file
with open('encryption.txt', 'w') as f:
    f.write(str(encrypted))

# Read the ciphertext from the file
with open('encryption.txt', 'r') as f:
    ciphertext = f.read()

# Convert the ciphertext from string back to tuple
ciphertext_tuple = ast.literal_eval(ciphertext)

# Decrypt the ciphertext
decrypted = key.decrypt(ciphertext_tuple)

# Print the decrypted message
print('Decrypted message:', decrypted.decode())