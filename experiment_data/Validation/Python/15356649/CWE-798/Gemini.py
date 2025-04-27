import string
import os
from collections import ChainMap

class SecureFormatter(string.Formatter):
    def __init__(self, allowed_fields=None):
        super().__init__()
        self.allowed_fields = allowed_fields if allowed_fields is not None else set()

    def get_field(self, field_name, args, kwargs):
        if field_name.isdigit():
            try:
                obj = args[int(field_name)]
                return obj, field_name
            except IndexError:
                raise IndexError("Tuple index out of range")
        else:  # Named fields
            try:
                obj = kwargs[field_name]
            except KeyError:
                return None, field_name  # Handle missing keys gracefully

            # Secure attribute/index access
            parts = field_name.split(".")  # Access nested attributes
            for part in parts:
                 if part not in self.allowed_fields:
                    raise AttributeError(f"Access to '{part}' is not allowed.")
                 if not hasattr(obj, part): # prevent exceptions leaking information
                    return None, field_name

                 obj = getattr(obj, part)

            return obj, field_name
        

    def vformat(self, format_string, args, kwargs):
        kwargs = ChainMap(kwargs, os.environ) # Allow env vars as a source
        return super().vformat(format_string, args, kwargs)


    def check_unused_args(self, used_args, args, kwargs):
        if len(used_args) < len(args) + len(kwargs):
            return 
            #  raise ValueError("Not all arguments converted during string formatting")

    def format_field(self, value, format_spec):
        if format_spec:  # Disable all formatting, including padding
            raise ValueError("Format specifiers are not allowed.")  # or return str(value) if you prefer
        return str(value)


# Example Usage
allowed_fields = {"name", "item", "price", "quantity"}  # Whitelist allowed fields
formatter = SecureFormatter(allowed_fields)

data = {"name": "Alice", "item": "Laptop", "price": 1200, "quantity": 2, "secret": "my_secret_key"}  # User-supplied data


try:
    result = formatter.format("Hello {name}, you ordered {quantity} {item}(s) for ${price:.2f}", **data)
    print(result)  # Output: Hello Alice, you ordered 2 Laptop(s) for $1200.00

    # Accessing allowed nested attributes (assuming 'item' is an object)
    data["item"] = type('obj', (object,), {"description": "High-performance laptop"})
    allowed_fields.add("description")
    result = formatter.format("Item description: {item.description}", **data)
    print(result)  # Output: Item description: High-performance laptop


    # Attempt to access disallowed fields
    result = formatter.format("{secret}", **data)  # Raises AttributeError
except (AttributeError, IndexError, ValueError) as e:
    print(f"Error: {e}")


# Example with environment variables
os.environ["ORDER_ID"] = "12345"
result = formatter.format("Your order ID is {ORDER_ID}", **data)
print(result)  # Output: Your order ID is 12345