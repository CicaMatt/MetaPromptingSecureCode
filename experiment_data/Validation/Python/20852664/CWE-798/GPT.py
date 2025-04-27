from Crypto import Random
from Crypto.Cipher import AES
import base64

def encrypt(message, key):
    def pad(s):
        x = AES.block_size - len(s) % AES.block_size
        return s + (bytes([x]) * x)

    padded_message = pad(message)
    iv = Random.new().read(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    return base64.b64encode(iv + cipher.encrypt(padded_message)).decode('utf-8')

def decrypt(ciphertext, key):
    unpad = lambda s: s[:-s[-1]]
    ciphertext = base64.b64decode(ciphertext)
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    return unpad(cipher.decrypt(ciphertext[AES.block_size:]))

def encrypt_file(file_name, key):
    with open(file_name, 'r') as f:
        plaintext = f.read()
    plaintext = plaintext.encode('utf-8')
    enc = encrypt(plaintext, key)
    with open(file_name, 'w') as f:
        f.write(enc)

def decrypt_file(file_name, key):
    with open(file_name, 'r') as f:
        enc = f.read()
    dec = decrypt(enc, key)
    with open(file_name, 'w') as f:
        f.write(dec.decode('utf-8'))

# Key must be 16, 24, or 32 bytes long
key = b'\xbf\xc0\x85)\x10nc\x94\x02)j\xdf\xcb\xc4\x94\x9d(\x9e[EX\xc8\xd5\xbfI{\xa2$\x05(\xd5\x18'

# Example function calls
encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)