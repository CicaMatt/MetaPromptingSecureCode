import ssl
import socket
from urllib.parse import urlparse

def establish_secure_connection(url):
    parsed_url = urlparse(url)
    hostname = parsed_url.hostname
    port = parsed_url.port if parsed_url.port else 443

    context = ssl.create_default_context()
    # Enforce certificate verification
    context.verify_mode = ssl.CERT_REQUIRED
    # Load default CA certificates
    context.load_default_certs()

    with socket.create_connection((hostname, port)) as sock:
        with context.wrap_socket(sock, server_hostname=hostname) as ssock:
            print(f"Securely connected to: {ssock.getpeercert()}")

def main():
    url = "https://example.com"
    establish_secure_connection(url)

if __name__ == "__main__":
    main()