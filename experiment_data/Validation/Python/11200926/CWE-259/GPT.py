import os
from scrapy.spiders import Spider
from scrapy.http import FormRequest, Request
from selenium import webdriver

# Make sure to set the following environment variables in your shell or IDE.
# export WEBSITE_USERNAME="your_username"
# export WEBSITE_PASSWORD="your_password"
username = os.getenv('WEBSITE_USERNAME')
password = os.getenv('WEBSITE_PASSWORD')

if not username or not password:
    raise EnvironmentError("Please set the WEBSITE_USERNAME and WEBSITE_PASSWORD environment variables")

class MySpider(Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def get_cookies(self):
        driver = webdriver.Firefox()
        driver.implicitly_wait(30)
        base_url = "http://www.my_domain.com/"
        driver.get(base_url)
        driver.find_element_by_name("USER").clear()
        driver.find_element_by_name("USER").send_keys(username)
        driver.find_element_by_name("PASSWORD").clear()
        driver.find_element_by_name("PASSWORD").send_keys(password)
        driver.find_element_by_name("submit").click()
        
        cookies = driver.get_cookies()
        driver.quit()
        return {cookie['name']: cookie['value'] for cookie in cookies}

    def parse(self, response):
        # Fetch cookies using Selenium and store them
        my_cookies = self.get_cookies()
        
        # Create a new request with the cookies
        return Request(
            url="http://my_domain.com/",
            cookies=my_cookies,
            callback=self.after_login
        )

    def after_login(self, response):
        # For demonstration, printing the title of the page
        print("Logged in:", response.css('title::text').get())