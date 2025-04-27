import string
import sys
from collections.abc import Mapping

class SecureFormatter(string.Formatter):
    def __init__(self, max_padding=100):
        self.max_padding = max_padding

    def get_field(self, field_name, args, kwargs):
        # Whitelist attribute and index access
        if isinstance(field_name, str):
            if field_name.startswith('__') or field_name.endswith('__'):
                raise ValueError("Access to dunder attributes is not allowed")
            if '.' in field_name:
                raise ValueError("Access to nested attributes is not allowed")
            if '[' in field_name or ']' in field_name:
                raise ValueError("Access to indices is not allowed")
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        # Disable padding beyond a certain limit
        if format_spec:
            if '>' in format_spec or '<' in format_spec or '^' in format_spec:
                padding_length = int(format_spec.strip('><^=+'))
                if padding_length > self.max_padding:
                    raise ValueError(f"Padding length exceeds maximum allowed ({self.max_padding})")
        return super().format_field(value, format_spec)

    def vformat(self, format_string, args, kwargs):
        try:
            return super().vformat(format_string, args, kwargs)
        except (ValueError, KeyError, IndexError) as e:
            raise ValueError(f"Formatting error: {e}")

# Example usage
if __name__ == "__main__":
    formatter = SecureFormatter(max_padding=100)
    try:
        # Safe usage
        safe_output = formatter.format("Hello, {name}!", name="Alice")
        print(safe_output)  # Output: Hello, Alice!

        # Attempt to use excessive padding
        # This will raise a ValueError
        # unsafe_output = formatter.format("Hello, {:>9999999999}!", "Alice")

        # Attempt to access dunder attributes
        # This will raise a ValueError
        # unsafe_output = formatter.format("Hello, {__class__}!", object())

        # Attempt to access nested attributes
        # This will raise a ValueError
        # unsafe_output = formatter.format("Hello, {obj.__class__}!", obj=object())

    except ValueError as e:
        print(f"Error: {e}", file=sys.stderr)