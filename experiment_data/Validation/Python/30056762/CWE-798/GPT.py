from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random

# Generate RSA keys
random_generator = Random.new().read
key = RSA.generate(2048, random_generator)  # Increase the key size for better security
public_key = key.publickey()

# Initialize cipher objects for encryption and decryption
cipher_encrypt = PKCS1_OAEP.new(public_key)
cipher_decrypt = PKCS1_OAEP.new(key)

# Original message
original_message = "encrypt this message"
original_message_bytes = original_message.encode()  # Convert message to bytes

# Encrypt the message
encrypted_message = cipher_encrypt.encrypt(original_message_bytes)

# Save the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted_message)

# Read the encrypted message from the file
with open('encryption.txt', 'rb') as f:
    encrypted_message_from_file = f.read()

# Decrypt the message
decrypted_message_bytes = cipher_decrypt.decrypt(encrypted_message_from_file)
decrypted_message = decrypted_message_bytes.decode()  # Convert bytes back to string

print('Encrypted Message:', encrypted_message)
print('Decrypted Message:', decrypted_message)