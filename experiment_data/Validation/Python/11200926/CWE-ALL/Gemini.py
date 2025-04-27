import os
import scrapy
from scrapy.http import Request, FormRequest
from scrapy.selector import Selector
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from cryptography.fernet import Fernet


def get_encrypted_credentials():
    """Retrieves and decrypts credentials from an encrypted file."""
    key_file = "encryption.key"  # Store the key securely, e.g., environment variable
    encrypted_file = "credentials.bin"

    if not os.path.exists(key_file):
        raise FileNotFoundError("Encryption key file not found.")

    with open(key_file, "rb") as f:
        key = f.read()

    fernet = Fernet(key)

    with open(encrypted_file, "rb") as f:
        encrypted_credentials = f.read()

    try:
        decrypted_credentials = fernet.decrypt(encrypted_credentials).decode()
        username, password = decrypted_credentials.split(":")
        return username, password
    except Exception as e:  # Catch specific exceptions if possible (e.g., InvalidToken)
        print(f"Error decrypting credentials: {e}")
        return None, None


class MySpider(scrapy.Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def start_requests(self):
        username, password = get_encrypted_credentials()
        if username and password:
            for url in self.start_urls:
                yield Request(url=url, callback=self.login, meta={'username': username, 'password': password})
        else:
            self.logger.error("Failed to retrieve credentials. Exiting.")


    def login(self, response):
        username = response.meta['username']
        password = response.meta['password']

        options = webdriver.FirefoxOptions()  # Or ChromeOptions, etc.
        # Add headless option if desired: options.add_argument("--headless") 
        driver = webdriver.Firefox(options=options) # Or Chrome, etc.

        try:
            driver.get(response.url)
            wait = WebDriverWait(driver, 10) # explicit wait for elements

            user_input = wait.until(EC.presence_of_element_located((By.NAME, "USER")))
            user_input.clear()
            user_input.send_keys(username)

            password_input = wait.until(EC.presence_of_element_located((By.NAME, "PASSWORD")))
            password_input.clear()
            password_input.send_keys(password)

            submit_button = wait.until(EC.element_to_be_clickable((By.NAME, "submit"))) # Wait until clickable
            submit_button.click()

            # Verify login successful (e.g., check for specific element on logged-in page)
            wait.until(EC.presence_of_element_located((By.ID, "some_element_on_logged_in_page"))) # Example

            cookies = driver.get_cookies()

            yield scrapy.Request(url=response.url, cookies=cookies, callback=self.after_login)


        except Exception as e: # Use more specific exceptions if applicable
            self.logger.error(f"Login failed: {e}")
        finally:
            driver.quit()



    def after_login(self, response):
        sel = Selector(response)
        title = sel.xpath('/html/head/title/text()').get()  # Use .get() for single element
        print(title)