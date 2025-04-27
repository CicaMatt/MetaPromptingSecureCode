from string import Formatter
import sys

class SafeFormatter(Formatter):
    def __init__(self, max_width=100, allowed_attrs=None):
        super().__init__()
        self.max_width = max_width
        self.allowed_attrs = allowed_attrs or set()

    def vformat(self, format_string, args, kwargs):
        try:
            return super().vformat(format_string, args, kwargs)
        except (IndexError, KeyError, AttributeError):
            return "<formatting error>"

    def parse(self, format_string):
        for literal_text, field_name, format_spec, conversion in super().parse(format_string):
            if field_name is not None:
                # Limit format specifier width
                if format_spec:
                    parts = format_spec.split()
                    new_parts = []
                    for part in parts:
                        if part.startswith(('>', '<', '^', '=')):  # Alignment and padding
                            try:
                                _, width = part.split(part[0], 1)
                                if width.isdigit() and int(width) > self.max_width:
                                    width = str(self.max_width)
                                new_part = part[0] + width
                            except ValueError: # Handles cases like "{:>}"
                                new_part = part[0]  # Keep alignment without width
                        else:
                            new_part = part
                        new_parts.append(new_part)
                    format_spec = " ".join(new_parts)

            yield literal_text, field_name, format_spec, conversion



    def get_field(self, field_name, args, kwargs):
        if field_name.isdigit():
            try:
                return super().get_field(field_name, args, kwargs)  # Handle positional arguments
            except IndexError:
                return "<out of range>", field_name

        # Restrict attribute access
        parts = field_name.split('.', 1)

        try:
            obj, rest = parts
        except ValueError:
            obj = field_name
            rest = None


        try:
            first_obj = eval(obj, {}, kwargs) # For keyword arguments
        except (NameError, SyntaxError):
            if not obj.isdigit():  # Already handled positional args
                try: 
                    first_obj = getattr(args[0], obj)  # For attribute access on first positional argument
                except (AttributeError, IndexError):
                    return "<not found>", field_name

        if rest:
            if obj not in self.allowed_attrs:
                return "<access denied>", field_name # Check attribute name against whitelist
            for attr in rest.split('.'):
                 if obj + "." + attr not in self.allowed_attrs:
                     return "<access denied>", field_name
                 try:
                    first_obj = getattr(first_obj, attr)
                 except AttributeError:
                    return "<not found>", field_name
        return first_obj, field_name





# Example usage:

safe_formatter = SafeFormatter(allowed_attrs={"user.name", "user.id"})
user = type('User', (object,), {'name': 'Alice', 'id': 123, 'secret': 'DoNotShow'})()

print(safe_formatter.format("Hello, {user.name}! Your ID is {user.id}.  {user.secret}", user)) # Allowed
print(safe_formatter.format("Hello, {user.__class__}", user)) # Denied - dunder attribute
print(safe_formatter.format("{:>50}", "short")) # Padding within limits
print(safe_formatter.format("{:>9999999999}", "boom")) # Padding limited
print(safe_formatter.format("{[0]}", [1,2,3])) # Positional argument access
print(safe_formatter.format("{[3]}", [1,2,3])) # Out of range
print(safe_formatter.format("{foo}", foo="bar")) # Keyword argument access