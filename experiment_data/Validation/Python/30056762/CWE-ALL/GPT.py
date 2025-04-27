from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
from Crypto import Random
import ast

def generate_key_pair():
    random_generator = Random.new().read
    key = RSA.generate(2048, random_generator)  # Use 2048-bit RSA key for stronger security
    public_key = key.publickey()
    return key, public_key

def encrypt_message(public_key, message):
    # Utilize PKCS1_OAEP for encryption which is more secure
    cipher = PKCS1_OAEP.new(public_key)
    encrypted_message = cipher.encrypt(message.encode('utf-8'))
    return encrypted_message

def decrypt_message(private_key, encrypted_message):
    try:
        cipher = PKCS1_OAEP.new(private_key)
        decrypted_message = cipher.decrypt(encrypted_message)
        return decrypted_message.decode('utf-8')
    except (ValueError, TypeError) as e:
        print(f"Decryption failed: {e}")
        return None

def write_to_file(file_name, data):
    with open(file_name, 'wb') as f:
        f.write(data)

def read_from_file(file_name):
    with open(file_name, 'rb') as f:
        return f.read()

# Key generation
private_key, public_key = generate_key_pair()

# Encrypt the message
message = "Encrypt this message securely."
encrypted_message = encrypt_message(public_key, message)
print('Encrypted message (byte format):', encrypted_message)

# Write encrypted message to a file
write_to_file('encryption.txt', encrypted_message)

# Read encrypted message from the file
encrypted_message_from_file = read_from_file('encryption.txt')

# Decrypt the message
decrypted_message = decrypt_message(private_key, encrypted_message_from_file)
if decrypted_message:
    print('Decrypted message:', decrypted_message)