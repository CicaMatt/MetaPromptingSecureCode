import os
from scrapy.spider import BaseSpider
from scrapy.http import Request
from scrapy.selector import Selector
from selenium import webdriver
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = [os.getenv("START_URL", "http://my_domain.com/")]  # Use env variable or default

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        self.username = os.getenv("MY_USERNAME")  # Retrieve credentials from environment
        self.password = os.getenv("MY_PASSWORD")
        if not self.username or not self.password:
            raise ValueError("MY_USERNAME and MY_PASSWORD environment variables must be set.")

    def get_cookies(self):
        options = webdriver.FirefoxOptions()  # Or ChromeOptions, etc.
        options.add_argument("--headless") # Run Selenium in headless mode
        driver = webdriver.Firefox(options=options) # Or webdriver.Chrome, etc.
        driver.implicitly_wait(30)
        driver.get(self.start_urls[0])
        driver.find_element_by_name("USER").clear()
        driver.find_element_by_name("USER").send_keys(self.username) # Use stored credentials
        driver.find_element_by_name("PASSWORD").clear()
        driver.find_element_by_name("PASSWORD").send_keys(self.password)
        driver.find_element_by_name("submit").click()
        cookies = driver.get_cookies()
        driver.quit() # Use quit instead of close to ensure all resources are released
        return cookies


    def parse(self, response):
        cookies = self.get_cookies()
        return Request(url=self.start_urls[0], cookies=cookies, callback=self.after_login)


    def after_login(self, response):
        sel = Selector(response) # Use Selector instead of deprecated HtmlXPathSelector
        title = sel.xpath('/html/head/title/text()').get() # Extract title using XPath
        print(title)

        # Continue scraping other data as needed...
        # ...