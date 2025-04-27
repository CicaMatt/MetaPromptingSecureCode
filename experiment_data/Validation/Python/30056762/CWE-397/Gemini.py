import Crypto
from Crypto.PublicKey import RSA
from Crypto import Random
import ast  # For safely evaluating the string representation of the tuple

random_generator = Random.new().read
key = RSA.generate(1024, random_generator)  # generate public and private keys

publickey = key.publickey  # pub key export for exchange

message = 'encrypt this message'
encrypted = publickey.encrypt(message.encode('utf-8'), 32) # Encrypt bytes, not strings. Add padding.

print('encrypted message:', encrypted)  # ciphertext

# Write the encrypted tuple as a string literal
with open('encryption.txt', 'w') as f:
    f.write(repr(encrypted))  # Use repr for unambiguous representation

# Decrypted code below
try:
    with open('encryption.txt', 'r') as f:
        encrypted_from_file_str = f.read()
        encrypted_from_file = ast.literal_eval(encrypted_from_file_str) # Safely convert string back to tuple


    decrypted = key.decrypt(encrypted_from_file).decode('utf-8') # Decrypt and decode back to string

    print('decrypted', decrypted)

    with open('encryption.txt', 'w') as f:
        f.write(str(encrypted_from_file) + '\n')
        f.write(decrypted)

except (ValueError, SyntaxError, TypeError) as e:
    print(f"Error reading or decrypting the ciphertext: {e}")
except Exception as e: # This broad except should be avoided in production but remains for demonstration
    print(f"An unexpected error occurred: {e}")