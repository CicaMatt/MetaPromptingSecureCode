import os
import string
from collections import ChainMap

class SecureFormatter(string.Formatter):
    def __init__(self, allowed_fields=None):
        super().__init__()
        self.allowed_fields = allowed_fields or set()

    def get_field(self, field_name, args, kwargs):
        if field_name.startswith("_"):  # Block access to dunder methods/attributes
            raise ValueError("Access to private attributes is prohibited.")

        try:
            obj, first = super().get_field(field_name, args, kwargs)
        except (IndexError, KeyError, AttributeError):
            raise ValueError(f"Invalid field: {field_name}")


        parts = field_name.split(".")
        for part in parts[1:]:
            if self.allowed_fields and part not in self.allowed_fields:
                raise ValueError(f"Access to field '{part}' is not allowed.")
            try:
                obj = getattr(obj, part)
            except AttributeError:
                raise ValueError(f"Invalid field: {field_name}")


        return obj, first

    def vformat(self, format_string, args, kwargs):
        try:
            return super().vformat(format_string, args, kwargs)
        except ValueError as e:
            # Log the error for debugging.  
            #  IMPORTANT: Do NOT leak sensitive information in error messages!
            print(f"Formatting error: {e}")  
            return "Formatting error"  # Return a generic error message to the user



# Example usage with environment variables for secrets
API_KEY = os.environ.get("API_KEY")
DATABASE_URL = os.environ.get("DATABASE_URL")

user_data = {"name": "Alice", "id": 123}


allowed_fields = {"name", "id"} # Explicitly whitelist allowed fields
formatter = SecureFormatter(allowed_fields)

# Safe formatting
try:
    formatted_string = formatter.format("Hello, {name}! Your ID is {id}.", **user_data)
    print(formatted_string)

    # Attempting to access a disallowed field
    formatted_string = formatter.format("Trying to access disallowed field: {id.__class__}", **user_data)
    print(formatted_string) 

except ValueError as e:
    print(f"Error: {e}")



# Demonstrate integrating with 'secrets' for sensitive data
try:
    import secrets
    secret_token = secrets.token_hex(16)
    # Store this token securely, e.g., in a database.  Do not embed in the format string.
    formatted_with_secret = formatter.format("Your secure token (retrieved separately): {token}", token=secret_token)
    print(formatted_with_secret)
except ImportError: # secrets is available from Python 3.6+
    print("secrets module not available. Using a placeholder.")
    formatted_with_secret = "secrets module not available."



# Example of secure database URL construction (avoid string formatting where possible)
import psycopg2 # Example database driver
try:
    conn = psycopg2.connect(DATABASE_URL)
    # ... database operations ...
    conn.close()
except psycopg2.Error as e:
    print(f"Database error: {e}")