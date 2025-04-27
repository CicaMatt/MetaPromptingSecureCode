import Crypto
from Crypto.PublicKey import RSA
from Crypto import Random
from Crypto.Cipher import PKCS1_OAEP
import base64

# Generate RSA key pair
random_generator = Random.new().read
key = RSA.generate(2048, random_generator)  # Use a stronger key size (2048 bits or higher)
private_key = key.exportKey()  # Store the private key securely (e.g., encrypted file, secrets manager)
public_key = key.publickey().exportKey()

# Encrypt the message
message = 'encrypt this message'
cipher = PKCS1_OAEP.new(key) #  Use PKCS1_OAEP for better security
encrypted_message = cipher.encrypt(message.encode('utf-8')) 

# Encode the ciphertext for storage (e.g., Base64)
encrypted_message_b64 = base64.b64encode(encrypted_message).decode('utf-8')

# Write the encoded ciphertext to a file
with open('encryption.txt', 'w') as f:
    f.write(encrypted_message_b64)


# -------------------- Decryption --------------------

# Read the encoded ciphertext from the file
with open('encryption.txt', 'r') as f:
    encrypted_message_b64 = f.read()

# Decode the ciphertext from Base64
encrypted_message = base64.b64decode(encrypted_message_b64)

# Load the private key (from secure storage in a real application).  
#  ***In real applications NEVER hardcode it***. This example
# uses `import_key` for demonstration within a single script.
private_key_obj = RSA.importKey(private_key)

# Decrypt the message
cipher = PKCS1_OAEP.new(private_key_obj)
decrypted_message = cipher.decrypt(encrypted_message).decode('utf-8')


print('Decrypted:', decrypted_message)

# Securely delete the private key from memory when done
del private_key
del private_key_obj