import string

class SecureFormatter(string.Formatter):
    def __init__(self):
        super().__init__()

    def get_field(self, field_name, args, kwargs):
        # Implement a whitelist for attribute/index access
        if not self.is_allowed_field(field_name):
            raise ValueError(f"Access to field '{field_name}' is not permitted.")
        return super().get_field(field_name, args, kwargs)

    def is_allowed_field(self, field_name):
        # Implement field whitelisting logic, for example:
        allowed_fields = {"name", "age", "location"}  # Example allowed field names
        return field_name in allowed_fields

    def format_field(self, value, format_spec):
        # Check for dangerous padding sizes
        if format_spec and isinstance(format_spec, str) and len(format_spec) > 50:
            raise ValueError("Padding length exceeds allowed limit.")
        return super().format_field(value, format_spec)

    def format(self, format_string, *args, **kwargs):
        try:
            return super().format(format_string, *args, **kwargs)
        except Exception as e:
            self.log_error(f"Formatting error: {e}")
            raise  # Re-raise with additional context if needed

    def log_error(self, message):
        # Implement structured logging, just printing for now
        print(f"ERROR: {message}")

# Example usage
formatter = SecureFormatter()

try:
    formatted_string = formatter.format(
        "Hello, {name}. You have {padding} messages.",
        name="Alice",
        padding="1234567890"
    )
    print(formatted_string)
except Exception as ex:
    print(f"An exception occurred: {ex}")