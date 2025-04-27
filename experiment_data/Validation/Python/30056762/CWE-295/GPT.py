from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto.Random import get_random_bytes

# Generate RSA key pair
key = RSA.generate(2048)  # Use a 2048-bit key for better security
public_key = key.publickey()

# Convert message to bytes; encryption requires byte input
message = b'encrypt this message'

# Encrypt message with public key using PKCS1_OAEP
cipher = PKCS1_OAEP.new(public_key)
encrypted_message = cipher.encrypt(message)

# Write the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read the encrypted message from the file
with open('encryption.txt', 'rb') as f:
    encrypted_message_from_file = f.read()

# Decrypt the message using the private key
decrypt_cipher = PKCS1_OAEP.new(key)
decrypted_message = decrypt_cipher.decrypt(encrypted_message_from_file)

# Print the decrypted message
print('Decrypted message:', decrypted_message.decode('utf-8'))