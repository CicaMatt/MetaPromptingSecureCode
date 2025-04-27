import scrapy
from scrapy.http import Request, FormRequest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


class MySpider(scrapy.Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def start_requests(self):
        yield Request(url=self.start_urls[0], callback=self.get_cookies)

    def get_cookies(self, response):
        options = webdriver.FirefoxOptions()  # Or ChromeOptions, etc.
        options.add_argument("--headless")  # Optional: Run Selenium headless
        driver = webdriver.Firefox(options=options) # Or webdriver.Chrome(options=options)
        driver.get("http://www.my_domain.com/")

        # Explicit wait for elements to be present before interacting
        wait = WebDriverWait(driver, 10)

        user_input = wait.until(EC.presence_of_element_located((By.NAME, "USER")))
        user_input.clear()
        user_input.send_keys("my_username")

        password_input = wait.until(EC.presence_of_element_located((By.NAME, "PASSWORD")))
        password_input.clear()
        password_input.send_keys("my_password")

        submit_button = wait.until(EC.presence_of_element_located((By.NAME, "submit"))) 
        submit_button.click()

        # Wait for the login to complete (e.g., check for a redirect or a specific element)
        # Example: wait.until(EC.url_contains("after_login_url"))  # Replace with actual URL

        cookies = driver.get_cookies()
        driver.quit()  # Close the browser entirely

        # Proceed with Scrapy requests, using the collected cookies
        yield scrapy.Request(
            url="http://my_domain.com/",
            cookies=cookies,
            callback=self.after_login
        )


    def after_login(self, response):
        # Check if login was successful (e.g., by looking for a specific element)
        if "Logout" in response.text: # Replace with actual check
            print("Login successful!")
            # Process the page content
            print(response.xpath('/html/head/title/text()').get())  # Use .get() for single element

        else:
            print("Login failed!")