import string
import sys
from collections.abc import Mapping

class SecureFormatter(string.Formatter):
    """
    A custom Formatter class that mitigates risks associated with untrusted input.
    This class disables arbitrary padding and whitelists attribute/index access.
    """

    def __init__(self, max_padding=100):
        """
        Initialize the formatter with a maximum allowed padding length.
        :param max_padding: Maximum allowed padding length (default: 100).
        """
        self.max_padding = max_padding

    def format_field(self, value, format_spec):
        """
        Override format_field to enforce a maximum padding length.
        :param value: The value to format.
        :param format_spec: The format specification.
        :return: Formatted string.
        """
        if format_spec:
            # Check for padding specifications (e.g., '{:>10}')
            if any(c in format_spec for c in ('<', '>', '^')):
                # Extract the padding length
                padding_length = ''.join(filter(str.isdigit, format_spec))
                if padding_length and int(padding_length) > self.max_padding:
                    raise ValueError(f"Padding length exceeds maximum allowed ({self.max_padding})")
        return super().format_field(value, format_spec)

    def get_field(self, field_name, args, kwargs):
        """
        Override get_field to whitelist attribute/index access.
        :param field_name: The field name to access.
        :param args: Positional arguments.
        :param kwargs: Keyword arguments.
        :return: The value of the field.
        """
        # Whitelist allowed attribute/index access patterns
        if '.' in field_name or '[' in field_name:
            raise ValueError("Accessing attributes or indices is not allowed")
        return super().get_field(field_name, args, kwargs)

def safe_format(format_string, *args, **kwargs):
    """
    Safely format a string using the SecureFormatter class.
    :param format_string: The format string.
    :param args: Positional arguments.
    :param kwargs: Keyword arguments.
    :return: Formatted string.
    """
    formatter = SecureFormatter()
    try:
        return formatter.format(format_string, *args, **kwargs)
    except (ValueError, KeyError, IndexError) as e:
        # Handle specific exceptions to avoid generic exception handling
        raise ValueError(f"Formatting error: {e}") from e

# Example usage
if __name__ == "__main__":
    try:
        user_input = "{name:>10}"
        formatted_output = safe_format(user_input, name="Alice")
        print(formatted_output)  # Output: "     Alice"
    except ValueError as e:
        print(f"Error: {e}")