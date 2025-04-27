from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random

# Generate key pair
random_generator = Random.new().read
key = RSA.generate(2048, random_generator)

# Export public and private keys
private_key = key.export_key()
public_key = key.publickey().export_key()

# Encrypt a message
message = b'encrypt this message'
cipher = PKCS1_OAEP.new(RSA.import_key(public_key))
encrypted_message = cipher.encrypt(message)

# Write ciphertext to file (use binary mode)
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read ciphertext from file
with open('encryption.txt', 'rb') as f:
    encrypted_message_from_file = f.read()

# Decrypt the message
cipher = PKCS1_OAEP.new(RSA.import_key(private_key))
decrypted_message = cipher.decrypt(encrypted_message_from_file)

# Print the results
print('Encrypted message:', encrypted_message)
print('Decrypted message:', decrypted_message.decode('utf-8'))