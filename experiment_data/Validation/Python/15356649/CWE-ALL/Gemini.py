import string
import sys

from string import Formatter


class SecureFormatter(Formatter):
    SAFE_CHARS = set(string.ascii_letters + string.digits + '_')

    def __init__(self, max_width=100):
        super().__init__()
        self.max_width = max_width

    def vformat(self, format_string, args, kwargs):
        if len(format_string) > 1000:  # Limit format string length
            raise ValueError("Format string too long")
        return super().vformat(format_string, args, kwargs)

    def get_field(self, field_name, args, kwargs):
        # Restrict attribute access - Only allow alphanumeric and underscore
        if not all(c in self.SAFE_CHARS for c in field_name):
            raise ValueError("Invalid field name")

        try:
             val = super().get_field(field_name, args, kwargs)
        except (AttributeError, IndexError, KeyError) as e:
            raise ValueError("Field access error") from e  # More specific exception
        
        return val


    def format_field(self, value, format_spec):
        if format_spec.startswith('>'):
            try:
                width = int(format_spec[1:])
                if width > self.max_width:
                    raise ValueError("Excessive width")
            except ValueError:
                raise ValueError("Invalid width specifier")
        elif format_spec:
            # Other potentially dangerous format specifiers could be checked/limited here as needed
            pass
        return super().format_field(value, format_spec)




# Example usage (demonstrating credential handling best practice outside of formatter):

import os
from cryptography.fernet import Fernet

# Fetch key from secure storage (environment variable for this example – NOT for production)
# In a real app, use dedicated secrets management
encryption_key = os.environ.get("ENCRYPTION_KEY")

if not encryption_key:
    raise EnvironmentError("Encryption key not found.")

cipher = Fernet(encryption_key)

def get_decrypted_credential(encrypted_credential):
    try:
        decrypted_bytes = cipher.decrypt(encrypted_credential.encode()) #handles invalid encrypted format
        return decrypted_bytes.decode()
    except Exception as e:
        # Log the error for debugging and audit trails
        print(f"Error decrypting credential: {e}", file=sys.stderr) #Do not leak sensitive information
        raise ValueError("Invalid credential") from e  # Generic message to avoid info leakage


encrypted_db_password = os.environ.get("ENCRYPTED_DB_PASSWORD") #Retrieve the encrypted password

if not encrypted_db_password:
    raise EnvironmentError("Encrypted database password not found.")



db_password = get_decrypted_credential(encrypted_db_password)
db_user = "secure_user" #Non sensitive data

sf = SecureFormatter()

# safe_string = sf.format("Connecting to DB as user {db_user}...", db_user=db_user)
# print(safe_string)

# Demonstrating safe field access with a data object:
class Data:
    def __init__(self):
        self.public_field = "Public Data"
        self._private_field = "Private Data"  # Should not be accessible


data = Data()

safe_string = sf.format("Public data: {data.public_field}", data=data)
print(safe_string)




try:
    sf.format("Trying to access private data: {data._private_field}", data=data) # This should now raise an exception
except ValueError as e:
    print(f"Caught expected error: {e}")