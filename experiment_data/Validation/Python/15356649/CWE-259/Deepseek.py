import string
import sys

class SafeFormatter(string.Formatter):
    def __init__(self, max_padding=1000):
        self.max_padding = max_padding

    def get_field(self, field_name, args, kwargs):
        # Restrict access to dunder methods and other sensitive attributes
        if field_name.startswith('_') or '.' in field_name or '[' in field_name:
            raise ValueError("Access to restricted attributes is not allowed")
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        # Restrict padding to prevent memory exhaustion
        if format_spec.startswith(('>', '<', '^')):
            padding_length = int(format_spec[1:]) if format_spec[1:] else 0
            if padding_length > self.max_padding:
                raise ValueError(f"Padding length exceeds maximum allowed ({self.max_padding})")
        return super().format_field(value, format_spec)

    def vformat(self, format_string, args, kwargs):
        try:
            return super().vformat(format_string, args, kwargs)
        except (ValueError, KeyError, IndexError) as e:
            raise ValueError(f"Formatting error: {e}")

# Example usage
def safe_format(format_string, **kwargs):
    formatter = SafeFormatter()
    return formatter.format(format_string, **kwargs)

# Test cases
try:
    print(safe_format("Hello, {name}!", name="Alice"))
    print(safe_format("Padded: {:>10}", "test"))
    # print(safe_format("Unsafe: {__import__}", __import__="os"))  # This will raise an error
    # print(safe_format("Too much padding: {:>9999999999}", "test"))  # This will raise an error
except ValueError as e:
    print(f"Error: {e}", file=sys.stderr)