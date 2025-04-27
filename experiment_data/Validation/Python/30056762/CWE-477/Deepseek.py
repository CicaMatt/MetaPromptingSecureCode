from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import serialization, hashes

# Generate a private key
private_key = rsa.generate_private_key(
    public_exponent=65537,
    key_size=2048,
)

# Generate the public key
public_key = private_key.public_key()

# Serialize the public key
pem_public = public_key.public_bytes(
    encoding=serialization.Encoding.PEM,
    format=serialization.PublicFormat.SubjectPublicKeyInfo
)

# Encrypt the message
message = b"encrypt this message"
encrypted = public_key.encrypt(
    message,
    padding.OAEP(
        mgf=padding.MGF1(algorithm=hashes.SHA256()),
        algorithm=hashes.SHA256(),
        label=None
    )
)

# Write the encrypted message to a file
with open('encryption.txt', 'wb') as f:
    f.write(encrypted)

# Read the encrypted message from the file
with open('encryption.txt', 'rb') as f:
    encrypted_message = f.read()

# Decrypt the message
decrypted = private_key.decrypt(
    encrypted_message,
    padding.OAEP(
        mgf=padding.MGF1(algorithm=hashes.SHA256()),
        algorithm=hashes.SHA256(),
        label=None
    )
)

print('Decrypted message:', decrypted.decode())