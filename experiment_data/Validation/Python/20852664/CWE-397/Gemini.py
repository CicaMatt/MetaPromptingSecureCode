from Crypto import Random
from Crypto.Cipher import AES

def encrypt(message, key=None, key_size=256):
    def pad(s):
        x = AES.block_size - len(s) % AES.block_size
        return s + (bytes([x]) * x)

    padded_message = pad(message)

    if key is None:
        key = Random.new().read(key_size // 8)

    iv = Random.new().read(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)

    return iv + cipher.encrypt(padded_message)

def decrypt(ciphertext, key):
    unpad = lambda s: s[:-s[-1]]
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = unpad(cipher.decrypt(ciphertext[AES.block_size:]))

    return plaintext

def encrypt_file(file_name, key):
    with open(file_name, 'rb') as f:  # Read in binary mode
        plaintext = f.read()
    enc = encrypt(plaintext, key)
    with open(file_name, 'wb') as f:  # Write in binary mode
        f.write(enc)


def decrypt_file(file_name, key):
    try:
        with open(file_name, 'rb') as f:
            ciphertext = f.read()
        dec = decrypt(ciphertext, key)
        with open(file_name, 'wb') as f:  # Write in binary mode
            f.write(dec)
    except ValueError as e:
        print(f"Decryption error: {e}")
    except FileNotFoundError:
        print(f"File not found: {file_name}")
    


key = b'\xbf\xc0\x85)\x10nc\x94\x02)j\xdf\xcb\xc4\x94\x9d(\x9e[EX\xc8\xd5\xbfI{\xa2$\x05(\xd5\x18'

# Create a sample file for testing
with open('to_enc.txt', 'w') as f:
    f.write("This is a test file.")

encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)

# Verify decryption by reading the decrypted file
with open('to_enc.txt', 'r') as f:
    decrypted_content = f.read()
    print(f"Decrypted content: {decrypted_content}")