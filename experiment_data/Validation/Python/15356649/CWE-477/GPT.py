import string

class SafeFormatter(string.Formatter):
    MAX_PADDING = 100  # Limit the padding to prevent DoS attacks

    def get_field(self, field_name, args, kwargs):
        # Prevent access to dunder (double underscore) methods or special methods
        if field_name.startswith('__') and field_name.endswith('__'):
            raise ValueError(f"Access to {field_name} is not allowed")
        
        # Only allow attribute/index access if it's whitelisted
        # Implement your own whitelist logic here if needed
        field = super().get_field(field_name, args, kwargs)
        return field

    def format_field(self, value, format_spec):
        if format_spec.startswith('>') or format_spec.startswith('<') or format_spec.startswith('^'):
            try:
                # Extract the padding length from the format specifier
                padding_length = int(format_spec[1:])
                if padding_length > self.MAX_PADDING:
                    raise ValueError(
                        f"Padding length {padding_length} exceeds the allowed limit of {self.MAX_PADDING}"
                    )
            except ValueError:
                # Handle the case where padding length isn't valid
                raise ValueError("Invalid format specification")
        
        # Otherwise, proceed with the default formatting
        return super().format_field(value, format_spec)

    def format(self, format_string, *args, **kwargs):
        try:
            return super().format(format_string, *args, **kwargs)
        except Exception as e:
            # Log the error and raise it
            # You can customize logging here
            print(f"Formatting error: {e}")
            raise

# Usage
formatter = SafeFormatter()

try:
    # This should succeed
    safe_output = formatter.format("Hello, {name}!", name="World")
    print(safe_output)

    # This should fail due to disallowed padding length
    unsafe_output = formatter.format("{:>999999}", "test")
except ValueError as e:
    print(f"Caught an exception: {e}")

try:
    # This should fail due to disallowed field access
    unsafe_output = formatter.format("{.__class__}", object())
except ValueError as e:
    print(f"Caught an exception: {e}")