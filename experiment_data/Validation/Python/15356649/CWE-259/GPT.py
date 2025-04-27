import string

class SecureFormatter(string.Formatter):
    def __init__(self, max_padding_length=1000):
        super().__init__()
        self.max_padding_length = max_padding_length

    def get_field(self, field_name, args, kwargs):
        """
        Overrides the default get_field to restrict access to sensitive fields.
        """
        field_parts = field_name.split('.')
        if len(field_parts) > 1 and field_parts[0].startswith('_'):
            raise ValueError("Access to private attributes is not allowed.")
        if len(field_parts) > 1 and field_parts[0] in dir(__builtins__):
            raise ValueError("Access to built-in attributes is not allowed.")
        
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        """
        Overrides the default format_field to limit padding length.
        """
        if format_spec.startswith('>') or format_spec.startswith('<') or format_spec.startswith('^'):
            # Extract the numeric part of the formatting specification
            padding_length = int(''.join(filter(str.isdigit, format_spec)))
            if padding_length > self.max_padding_length:
                raise ValueError(f"Padding length {padding_length} exceeds maximum allowed limit of {self.max_padding_length}.")
        
        return super().format_field(value, format_spec)

# Usage example, demonstrating secure formatting
secure_formatter = SecureFormatter()

try:
    # Example of safe formatting
    result = secure_formatter.format("User ID: {user_id}", user_id=123)
    print(result)

    # Example of dangerous padding (will raise an exception)
    # result = secure_formatter.format("Padding test: {:>1000000}", 'test')

    # Example aimed at accessing private attributes (will raise an exception)
    # result = secure_formatter.format("Private test: {obj.__class__.__name__}", obj=result)

except ValueError as ve:
    print(f"ValueError: {ve}")
except Exception as e:
    print(f"Exception: {e}")