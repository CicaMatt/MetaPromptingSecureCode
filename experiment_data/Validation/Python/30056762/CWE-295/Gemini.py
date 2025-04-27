from Crypto.PublicKey import RSA
from Crypto import Random

random_generator = Random.new().read
key = RSA.generate(1024, random_generator) #generate pub and priv key

publickey = key.publickey # pub key export for exchange

encrypted = publickey.encrypt(b'encrypt this message', 32) # Encrypt bytes, not strings
#message to encrypt is in the above line 'encrypt this message'

print('encrypted message:', encrypted) #ciphertext

with open('encryption.txt', 'wb') as f:  # Write binary data
    f.write(encrypted[0]) # Write only the ciphertext (encrypted is a tuple)


#decrypted code below
with open('encryption.txt', 'rb') as f:
    message = f.read()

decrypted = key.decrypt(message)

print('decrypted:', decrypted.decode()) # Decode the decrypted bytes to string


with open('encryption.txt', 'wb') as f: # Write binary
    f.write(message) 
    f.write(decrypted)