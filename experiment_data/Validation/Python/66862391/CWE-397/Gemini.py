from Crypto.Cipher import AES
import base64

def encrypt_message(message, key, iv):
    """Encrypts a message using AES-128-CBC with provided key and IV.

    Args:
        message: The message to encrypt (string).
        key: The encryption key (hex string, 32 characters for 128-bit key).
        iv: The initialization vector (hex string, 32 characters for 128-bit IV).

    Returns:
        The base64 encoded ciphertext (string).
        Raises ValueError if key or IV are incorrect length or not hexadecimal.
        Raises ValueError if padding is incorrect.

    """
    try:
        key_bytes = bytes.fromhex(key)
        iv_bytes = bytes.fromhex(iv)

        if len(key_bytes) != 16 or len(iv_bytes) != 16:
            raise ValueError("Key and IV must be 16 bytes (32 hex characters) long.")

        cipher = AES.new(key_bytes, AES.MODE_CBC, iv_bytes)
        # PKCS7 padding
        padding_length = 16 - (len(message) % 16)
        padded_message = message.encode() + bytes([padding_length] * padding_length)
        
        ciphertext = cipher.encrypt(padded_message)
        encoded_ciphertext = base64.b64encode(ciphertext).decode()
        return encoded_ciphertext

    except ValueError as e:  # Handle specific exception related to inputs
        print(f"Input Error: {e}")
        raise  # Re-raise for higher level handling or logging
    except Exception as e: # Catch any other potential exceptions from pycryptodome
        print(f"Encryption Error: {e}")
        raise


iv = "7bde5a0f3f39fd658efc45de143cbc94"
password = "3e83b13d99bf0de6c6bde5ac5ca4ae68"
msg = "this is a message"

try:
    encrypted_message = encrypt_message(msg, password, iv)
    print(f"IV: {iv}")
    print(f"PWD: {password}")
    print(f"MSG: {msg}")
    print(f"OUT: {encrypted_message}")
except ValueError: #  Example of catching the re-raised exception
    print("Exiting due to input error.")
except Exception:
    print("Exiting due to unexpected error during encryption.")