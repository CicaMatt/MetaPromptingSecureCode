import string

class SafeFormatter(string.Formatter):

    def check_padding(self, format_spec):
        # Define a maximum padding length to prevent excessive memory allocation
        MAX_PADDING = 1000
        # Extract the padding length from the format specification, if present
        if ':' in format_spec:
            padding_spec = format_spec.split(':')[1]
            if '>' in padding_spec or '<' in padding_spec or '^' in padding_spec:
                padding_length = ''.join(filter(str.isdigit, padding_spec))
                if padding_length.isdigit() and int(padding_length) > MAX_PADDING:
                    raise ValueError("Padding length is too large")

    def get_field(self, field_name, args, kwargs):
        # Whitelist allowed attributes to prevent arbitrary attribute access
        allowed_attributes = {'name', 'age', 'email'}  # Add the safe attributes
        if '.' in field_name:
            first, rest = field_name.split('.', 1)
            if first not in allowed_attributes:
                raise AttributeError(f"Access to attribute '{first}' is denied.")
        return super().get_field(field_name, args, kwargs)

    def format_field(self, value, format_spec):
        self.check_padding(format_spec)
        return super().format_field(value, format_spec)

    def format(self, format_string, *args, **kwargs):
        try:
            return super().format(format_string, *args, **kwargs)
        except (ValueError, AttributeError, KeyError) as e:
            # Handle any formatting errors that arise
            raise ValueError(f"Formatting error: {e}")

# Usage Example
safe_formatter = SafeFormatter()
data = {'name': 'Alice', 'age': 30}

try:
    # Correct formatter usage
    result = safe_formatter.format("Name: {name}, Age: {age}", **data)
    print(result)

    # Attempt with an excessively large padding
    # This will raise an error
    result = safe_formatter.format("Padded number: {:>100000000}", 42)
    print(result)

    # Attempt with accessing an unlisted attribute
    # This will also raise an error
    result = safe_formatter.format("Email: {email}", **data)
    print(result)

except ValueError as e:
    print(e)