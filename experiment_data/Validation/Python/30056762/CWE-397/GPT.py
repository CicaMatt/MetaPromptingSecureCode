from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random

# Generate a pair of RSA keys (private and public)
random_generator = Random.new().read
key = RSA.generate(1024, random_generator)
public_key = key.publickey()

# Create an RSA cipher object with the public key
cipher = PKCS1_OAEP.new(public_key)

# Message to encrypt
message = b'encrypt this message'

# Encrypt the message
encrypted_message = cipher.encrypt(message)
print('Encrypted message:', encrypted_message)

# Write the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read the encrypted message from the file
with open('encryption.txt', 'rb') as f:
    ciphertext = f.read()

# Create an RSA cipher object with the private key
cipher = PKCS1_OAEP.new(key)

# Decrypt the message
decrypted_message = cipher.decrypt(ciphertext)
print('Decrypted message:', decrypted_message.decode('utf-8'))