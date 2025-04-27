import string
import sys

class SecureFormatter(string.Formatter):
    MAX_PADDING = 1000  # Define a reasonable maximum padding length

    def __init__(self, allowed_attributes=None):
        super().__init__()
        self.allowed_attributes = allowed_attributes or set()

    def format_field(self, value, format_spec):
        # Disable padding if it exceeds the maximum allowed length
        if format_spec and format_spec[0] in ('<', '>', '^'):
            padding_length = int(format_spec[1:]) if len(format_spec) > 1 else 0
            if padding_length > self.MAX_PADDING:
                raise ValueError(f"Padding length {padding_length} exceeds maximum allowed {self.MAX_PADDING}")
        return super().format_field(value, format_spec)

    def get_field(self, field_name, args, kwargs):
        # Whitelist attribute/index access
        if field_name in self.allowed_attributes:
            return super().get_field(field_name, args, kwargs)
        else:
            raise AttributeError(f"Access to attribute '{field_name}' is not allowed")

# Example usage
if __name__ == "__main__":
    # Define allowed attributes
    allowed_attributes = {'name', 'age'}

    # Create an instance of SecureFormatter with allowed attributes
    formatter = SecureFormatter(allowed_attributes)

    # Example data
    data = {
        'name': 'Alice',
        'age': 30,
        'secret': 's3cr3t'
    }

    # Safe formatting
    try:
        safe_output = formatter.format("Name: {name}, Age: {age}", **data)
        print(safe_output)  # Output: Name: Alice, Age: 30
    except (ValueError, AttributeError) as e:
        print(f"Error: {e}")

    # Attempt to access a disallowed attribute
    try:
        unsafe_output = formatter.format("Secret: {secret}", **data)
        print(unsafe_output)
    except (ValueError, AttributeError) as e:
        print(f"Error: {e}")  # Output: Error: Access to attribute 'secret' is not allowed

    # Attempt to use excessive padding
    try:
        padded_output = formatter.format("Name: {:>9999999999}", "Alice")
        print(padded_output)
    except (ValueError, AttributeError) as e:
        print(f"Error: {e}")  # Output: Error: Padding length 9999999999 exceeds maximum allowed 1000