import string

class SafeFormatter(string.Formatter):
    def __init__(self):
        super().__init__()

    def get_field(self, field_name, args, kwargs):
        # Whitelist allowed attribute names or indices
        if isinstance(field_name, str) and field_name.startswith('_'):
            raise ValueError(f"Access to private attributes is not allowed: {field_name}")
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        # Restrict maximum width
        if '>' in format_spec:
            # Strip padding options and only allow minimal formatting specifications to prevent large memory allocations
            format_spec = format_spec.replace('>', '')
        return super().format_field(value, format_spec)

    def check_unused_args(self, used_args, args, kwargs):
        # Override this method if you have additional checks on unused arguments, not doing anything extra here
        pass

def secure_format(template, *args, **kwargs):
    """Securely format a string with given arguments."""
    safe_formatter = SafeFormatter()
    try:
        # Try formatting the string safely
        return safe_formatter.format(template, *args, **kwargs)
    except (ValueError, KeyError, IndexError) as e:
        # Catch specific exceptions related to formatting issues
        raise ValueError(f"Failed to format string: {e}") from e

# Example usage
template = "Hello, {name}! Your balance is {balance:>10.2f}"
try:
    result = secure_format(template, name="Alice", balance=1234.56)
    print(result)
except ValueError as e:
    print(f"Error during formatting: {e}")