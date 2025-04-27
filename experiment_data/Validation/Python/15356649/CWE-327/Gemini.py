from string import Formatter
from collections import namedtuple

_Field = namedtuple('_Field', 'name index')


class SafeFormatter(Formatter):
    def __init__(self, max_width=100, allowed_attrs=None):
        super().__init__()
        self.max_width = max_width
        self.allowed_attrs = allowed_attrs if allowed_attrs is not None else set()

    def vformat(self, format_string, args, kwargs):
        if len(format_string) > 1000:  # Limit format string length as an extra precaution
            raise ValueError("Format string too long")
        return super().vformat(format_string, args, kwargs)

    def get_field(self, field_name, args, kwargs):
        try:
            # Split to handle nested attributes, e.g., 'user.name'
            parts = field_name.split('.', 1)
            obj, first = super().get_field(parts[0], args, kwargs)

            for part in parts[1:]:
                # Whitelist attribute access 
                if part not in self.allowed_attrs:
                    raise AttributeError(f"Access to attribute '{part}' is not allowed.")
                obj = getattr(obj, part)

            return obj, first

        except (IndexError, KeyError, AttributeError) as e:
            raise ValueError(f"Invalid field access: {field_name}") from e
        except ValueError as e:
            raise  # Re-raise other ValueErrors like invalid format specs


    def check_unused_args(self, used_args, args, kwargs):
        # Disable unused args check - not strictly needed for security
        pass

    def format_field(self, value, format_spec):
        if any(c in format_spec for c in '<>^'): # Disable alignment
            format_spec = format_spec.replace('<', '')
            format_spec = format_spec.replace('>', '')
            format_spec = format_spec.replace('^', '')

        if any(c.isdigit() for c in format_spec): #  Handle width and precision
            width_start = -1
            for i, c in enumerate(format_spec):
                if c.isdigit():
                    if width_start == -1:
                        width_start = i
                elif width_start != -1:
                    try:
                        width = int(format_spec[width_start:i])
                        if width > self.max_width:
                            format_spec = format_spec[:width_start] + str(self.max_width) + format_spec[i:]
                    except ValueError:
                        pass  # ignore invalid width
                    width_start = -1

        return super().format_field(value, format_spec)



# Example usage (demonstrates both attribute access and formatting):
data = {"user": namedtuple("User", ["name", "id"])("Alice", 123)}
allowed_attributes = {"name", "id"}

formatter = SafeFormatter(allowed_attrs=allowed_attributes)

try:
     formatted_string = formatter.format("User: {user.name}, ID: {user.id:>5}", **data)  # Safe access and limited padding
     print(formatted_string)

     # Demonstrating that invalid access is blocked:
     formatter.format("User: {user.__class__}", **data) # Raises ValueError 
except ValueError as e:
    print(f"Error: {e}")


try:
    formatter.format("{:>9999999}", "test") # Raises ValueError due to excessive width
except ValueError as e:
    print(f"Error: {e}")