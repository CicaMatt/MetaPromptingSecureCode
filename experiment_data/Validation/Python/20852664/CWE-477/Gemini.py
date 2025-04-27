from Crypto.Cipher import AES
import os

def encrypt(message, key=None, key_size=256):
    def pad(s):
        return s + (AES.block_size - len(s) % AES.block_size) * bytes([AES.block_size - len(s) % AES.block_size])

    padded_message = pad(message)

    if key is None:
        key = os.urandom(key_size // 8)

    iv = os.urandom(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)

    return iv + cipher.encrypt(padded_message)

def decrypt(ciphertext, key):
    def unpad(s):
        return s[:-s[-1]]
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = unpad(cipher.decrypt(ciphertext[AES.block_size:])) # Fixed: Removed slicing after unpadding

    return plaintext

def encrypt_file(file_name, key):
    with open(file_name, 'rb') as f:  # Fixed: Read in binary mode
        plaintext = f.read()
    enc = encrypt(plaintext, key)
    with open(file_name, 'wb') as f:  # Fixed: Write in binary mode
        f.write(enc)

def decrypt_file(file_name, key):
    with open(file_name, 'rb') as f:  # Fixed: Read in binary mode
        ciphertext = f.read()
    dec = decrypt(ciphertext, key)
    with open(file_name, 'wb') as f:  # Fixed: Write in binary mode
        f.write(dec)



key = b'\xbf\xc0\x85)\x10nc\x94\x02)j\xdf\xcb\xc4\x94\x9d(\x9e[EX\xc8\xd5\xbfI{\xa2$\x05(\xd5\x18'

encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)


with open('to_enc.txt', 'rb') as f:
    print(f.read())