from Crypto.PublicKey import RSA
from Crypto.PublicKey.RSA import RsaKey
from Crypto.Cipher import PKCS1_OAEP
import binascii

# Generate public and private keys
private_key: RsaKey = RSA.generate(2048)
public_key: RsaKey = private_key.publickey()

# Use PKCS1 OAEP for encryption/decryption
cipher = PKCS1_OAEP.new(public_key)

# Message to encrypt
message = 'encrypt this message'
encrypted_message = cipher.encrypt(message.encode())

print("Encrypted message:", binascii.hexlify(encrypted_message))

# Write the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read the encrypted message from the file
with open('encryption.txt', 'rb') as f:
    encrypted_message_from_file = f.read()

# Decrypt the message
cipher_decryptor = PKCS1_OAEP.new(private_key)
decrypted_message = cipher_decryptor.decrypt(encrypted_message_from_file)

print("Decrypted message:", decrypted_message.decode())

# Overwrite the file with decrypted message for demonstration (not secure in practice)
with open('encryption.txt', 'wb') as f:
    f.write(decrypted_message)