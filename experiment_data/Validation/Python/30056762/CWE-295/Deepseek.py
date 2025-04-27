from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random
import ast

# Generate public and private keys
random_generator = Random.new().read
key = RSA.generate(1024, random_generator)
publickey = key.publickey()

# Encrypt the message
cipher = PKCS1_OAEP.new(publickey)
message = 'encrypt this message'
encrypted = cipher.encrypt(message.encode())

print('Encrypted message:', encrypted)

# Write ciphertext to file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted)

# Decrypt the message
with open('encryption.txt', 'rb') as f:
    encrypted_message = f.read()

cipher = PKCS1_OAEP.new(key)
decrypted = cipher.decrypt(encrypted_message)

print('Decrypted message:', decrypted.decode())

# Write decrypted message to file
with open('decryption.txt', 'w') as f:
    f.write(decrypted.decode())