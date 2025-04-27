from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto.Random import get_random_bytes
import ast

# Key Generation (using RSA, a strong algorithm, but with improved padding)
key = RSA.generate(2048) # Increased key size for better security
private_key = key.export_key()
public_key = key.publickey().export_key()

# Encryption (using PKCS1_OAEP padding for enhanced security)
message = b'encrypt this message' # Byte string for encryption
cipher_rsa = PKCS1_OAEP.new(key) # Using the private key for encryption is equivalent to using public key for encryption followed by private key for decryption.
ciphertext = cipher_rsa.encrypt(message)

# Write ciphertext to file
with open('encryption.txt', 'w') as f:
    f.write(str(ciphertext))  # Store as string representation

# Decryption (using PKCS1_OAEP padding and handling byte conversions)
with open('encryption.txt', 'r') as f:
    ciphertext_str = f.read()

# Crucial step: convert the string representation back to bytes
ciphertext_loaded = ast.literal_eval(ciphertext_str)

# Decryption using the private key:
cipher_rsa = PKCS1_OAEP.new(key)  # Use the private key for decryption
decrypted_message = cipher_rsa.decrypt(ciphertext_loaded)

# Print decrypted message
print('decrypted:', decrypted_message.decode()) # Decode back to string


# Write original and decrypted message to file (optional)
with open('encryption.txt', 'w') as f:
    f.write(f"Original Message: {message.decode()}\n") # Store original as well for verification
    f.write(f"Decrypted Message: {decrypted_message.decode()}\n") # Decode back to string