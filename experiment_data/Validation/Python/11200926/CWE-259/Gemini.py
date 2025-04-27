import os
from scrapy.spider import BaseSpider
from scrapy.http import Request
from scrapy.selector import Selector
from selenium import webdriver
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

USERNAME = os.getenv("MY_USERNAME")
PASSWORD = os.getenv("MY_PASSWORD")

if USERNAME is None or PASSWORD is None:
    raise ValueError("Environment variables MY_USERNAME and MY_PASSWORD must be set.")


class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        self.driver = webdriver.Firefox()  # Initialize Selenium webdriver
        self.driver.implicitly_wait(30)

    def close(self, spider, reason):
        self.driver.quit()
        return super().close(spider, reason)


    def parse(self, response):
        return Request(url="http://www.my_domain.com/login",  # Correct login URL
                       callback=self.login)

    def login(self, response):
         # Perform login using Selenium
        self.driver.get("http://www.my_domain.com/login")  # Correct login URL
        self.driver.find_element_by_name("USER").clear()
        self.driver.find_element_by_name("USER").send_keys(USERNAME)
        self.driver.find_element_by_name("PASSWORD").clear()
        self.driver.find_element_by_name("PASSWORD").send_keys(PASSWORD)
        self.driver.find_element_by_name("submit").click()

        # Get cookies from Selenium and add them to Scrapy request
        cookies = self.driver.get_cookies()
        cookie_dict = {cookie['name']: cookie['value'] for cookie in cookies}


        # Navigate to target page after login
        return Request(url="http://my_domain.com/target_page",  # Replace with the actual target URL
                       cookies=cookie_dict,
                       callback=self.after_login)


    def after_login(self, response):
        # Process the page content
        sel = Selector(response) # Use Selector, not deprecated HtmlXPathSelector
        title = sel.xpath('/html/head/title/text()').get()  # Use .get() or .getall() for extraction
        print(title)


        # Example: Extract data
        # items = []
        # # Your extraction logic here
        # return items