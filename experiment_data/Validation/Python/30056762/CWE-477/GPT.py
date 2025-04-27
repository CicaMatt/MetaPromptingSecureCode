from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
import binascii

# Generate RSA key pair
key = RSA.generate(2048)
private_key = key
public_key = key.publickey()

# Create a cipher object using the public key
cipher_encrypt = PKCS1_OAEP.new(public_key)

# Encrypt the message
message = 'encrypt this message'
encrypted_message = cipher_encrypt.encrypt(message.encode('utf-8'))
print('Encrypted message:', binascii.hexlify(encrypted_message))

# Write the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read the encrypted message from a file
with open('encryption.txt', 'rb') as f:
    encrypted_message_from_file = f.read()

# Create a cipher object using the private key
cipher_decrypt = PKCS1_OAEP.new(private_key)

# Decrypt the message
decrypted_message = cipher_decrypt.decrypt(encrypted_message_from_file)
print('Decrypted message:', decrypted_message.decode('utf-8'))