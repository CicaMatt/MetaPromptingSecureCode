import ssl
import socket
import requests
from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.x509.oid import NameOID
from datetime import datetime

# Function to validate certificate
def validate_certificate(cert_pem):
    # Load the certificate
    cert = x509.load_pem_x509_certificate(cert_pem.encode(), default_backend())
    
    # Get certificate properties
    issuer = cert.issuer.get_attributes_for_oid(NameOID.COMMON_NAME)[0].value
    subject = cert.subject.get_attributes_for_oid(NameOID.COMMON_NAME)[0].value
    not_before = cert.not_valid_before
    not_after = cert.not_valid_after

    # Perform standard validations
    current_time = datetime.utcnow()
    if not (not_before <= current_time <= not_after):
        print("Certificate is expired or not yet valid")
        return False
    
    # Additional validations can be added here, such as checking the issuer against a whitelist
    
    print("Certificate is valid")
    return True


def fetch_certificate(hostname, port):
    context = ssl.create_default_context()
    conn = context.wrap_socket(socket.socket(socket.AF_INET), server_hostname=hostname)
    
    try:
        conn.connect((hostname, port))
        cert_der = conn.getpeercert(binary_form=True)
        cert_pem = ssl.DER_cert_to_PEM_cert(cert_der)
        return cert_pem
    except Exception as e:
        print(f"Error fetching certificate: {e}")
    finally:
        conn.close()


# Example usage
hostname = 'www.example.com'
port = 443
certificate_pem = fetch_certificate(hostname, port)

if certificate_pem and validate_certificate(certificate_pem):
    print(f"Successfully validated certificate for {hostname}")
else:
    print(f"Failed to validate certificate for {hostname}")