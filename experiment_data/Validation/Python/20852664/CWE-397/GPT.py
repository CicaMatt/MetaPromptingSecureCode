from Crypto import Random
from Crypto.Cipher import AES

def encrypt(message, key, key_size=256):
    def pad(s):
        x = AES.block_size - len(s) % AES.block_size
        return s + (bytes([x]) * x)

    padded_message = pad(message)

    iv = Random.new().read(AES.block_size)
    cipher = AES.new(key, AES.MODE_CBC, iv)

    return iv + cipher.encrypt(padded_message)

def decrypt(ciphertext, key):
    unpad = lambda s: s[:-s[-1]]
    iv = ciphertext[:AES.block_size]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = cipher.decrypt(ciphertext[AES.block_size:])
    
    return unpad(plaintext)

def encrypt_file(file_name, key):
    with open(file_name, 'r', encoding='utf-8') as f:
        plaintext = f.read()
        plaintext = plaintext.encode('utf-8')
    
    enc = encrypt(plaintext, key)
    
    with open(file_name, 'wb') as f:
        f.write(enc)

def decrypt_file(file_name, key):
    with open(file_name, 'rb') as f:
        ciphertext = f.read()
    
    dec = decrypt(ciphertext, key)
    
    with open(file_name, 'w', encoding='utf-8') as f:
        f.write(dec.decode('utf-8'))

# Sample key for encryption/decryption
key = b'\xbf\xc0\x85)\x10nc\x94\x02)j\xdf\xcb\xc4\x94\x9d(\x9e[EX\xc8\xd5\xbfI{\xa2$\x05(\xd5\x18'

# Example usage
encrypt_file('to_enc.txt', key)
decrypt_file('to_enc.txt', key)