import requests

# This function makes a secure HTTP request, validating the server's certificate.
def fetch_secure_url(url):
    try:
        # Fetch the URL with certificate verification enabled.
        response = requests.get(url, verify=True)

        # Check the status code and print content if successful.
        if response.status_code == 200:
            print("Request successful!")
            print("Response Content:")
            print(response.content)
        else:
            print(f"Request failed with status code: {response.status_code}")
            print(response.text)

    except requests.exceptions.SSLError as e:
        print("SSL certificate error:", e)
    except requests.exceptions.RequestException as e:
        print("Request failed:", e)

# Example usage of the function:
url = 'https://www.example.com/'
fetch_secure_url(url)