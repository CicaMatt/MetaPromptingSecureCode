from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto.Random import get_random_bytes
import ast

# Generate RSA key pair
key = RSA.generate(2048)  # Use a stronger key size (2048 bits or higher)
private_key = key.export_key()
public_key = key.publickey().export_key()

# Encrypt the message
message = 'encrypt this message'.encode()  # Encode to bytes
cipher = PKCS1_OAEP.new(key)  # Use PKCS1_OAEP padding for security
ciphertext = cipher.encrypt(message)


# Write ciphertext to file (properly encoded)
with open('encryption.txt', 'w') as f:
    f.write(str(ciphertext))


# Decrypt the message
with open('encryption.txt', 'r') as f:
    ciphertext_from_file_str = f.read()
    ciphertext_from_file = ast.literal_eval(ciphertext_from_file_str)  # Safely convert string back to bytes


cipher_dec = PKCS1_OAEP.new(key)
decrypted_message = cipher_dec.decrypt(ciphertext_from_file)

print('Decrypted:', decrypted_message.decode()) # Decode from bytes to string



# Write decrypted message to file (optional)
with open('encryption.txt', 'w') as f:
    f.write(str(ciphertext)) # Rewriting the ciphertext, as per original example
    f.write('\n' + decrypted_message.decode())