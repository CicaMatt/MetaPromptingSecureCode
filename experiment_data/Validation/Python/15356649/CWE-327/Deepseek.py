import string
import sys

class SecureFormatter(string.Formatter):
    MAX_PADDING_LENGTH = 1000  # Define a reasonable maximum padding length

    def __init__(self, allowed_attributes=None):
        super().__init__()
        self.allowed_attributes = allowed_attributes or set()

    def get_field(self, field_name, args, kwargs):
        # Restrict access to attributes not in the whitelist
        if '.' in field_name:
            base, attr = field_name.split('.', 1)
            if attr not in self.allowed_attributes:
                raise ValueError(f"Access to attribute '{attr}' is not allowed")
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        # Limit padding length to prevent memory exhaustion
        if format_spec and format_spec[0] in ('<', '>', '^'):
            padding_length = int(format_spec[1:]) if len(format_spec) > 1 else 0
            if padding_length > self.MAX_PADDING_LENGTH:
                raise ValueError(f"Padding length exceeds maximum allowed ({self.MAX_PADDING_LENGTH})")
        return super().format_field(value, format_spec)

# Example usage
if __name__ == "__main__":
    # Define allowed attributes
    allowed_attrs = {'name', 'age'}

    # Create an instance of SecureFormatter
    formatter = SecureFormatter(allowed_attributes=allowed_attrs)

    # Example data
    data = {
        'name': 'Alice',
        'age': 30,
        'secret': 's3cr3t'
    }

    # Safe formatting
    try:
        safe_output = formatter.format("Name: {name}, Age: {age:>10}", **data)
        print(safe_output)
    except ValueError as e:
        print(f"Error: {e}")

    # Attempt to access a disallowed attribute
    try:
        unsafe_output = formatter.format("Secret: {secret}", **data)
        print(unsafe_output)
    except ValueError as e:
        print(f"Error: {e}")

    # Attempt to use excessive padding
    try:
        excessive_padding = formatter.format("Name: {name:>9999999999}", **data)
        print(excessive_padding)
    except ValueError as e:
        print(f"Error: {e}")