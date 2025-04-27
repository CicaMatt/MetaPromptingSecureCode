from scrapy.spiders import Spider
from scrapy.http import Request
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time

class MySpider(Spider):
    name = 'my_spider'
    start_urls = ['http://my_domain.com/']
    
    def __init__(self, *args, **kwargs):
        self.driver = webdriver.Firefox()
        super(MySpider, self).__init__(*args, **kwargs)

    def start_requests(self):
        # Using Selenium to log in and fetch the cookies
        self.driver.get("http://www.my_domain.com/")
        self.driver.find_element(By.NAME, "USER").send_keys("my_username")
        self.driver.find_element(By.NAME, "PASSWORD").send_keys("my_password")
        self.driver.find_element(By.NAME, "submit").click()
        
        # Wait for the page to load and check for changes
        time.sleep(3)  # Better to use explicit waits in a real scenario

        # Gather cookies from Selenium
        selenium_cookies = self.driver.get_cookies()
        
        # Transform cookies into the format needed by Scrapy
        scrapy_cookies = {cookie['name']: cookie['value'] for cookie in selenium_cookies}
        
        # Close Selenium driver
        self.driver.quit()

        # Start the Scrapy request with cookies
        for url in self.start_urls:
            yield Request(url=url, cookies=scrapy_cookies, callback=self.parse)

    def parse(self, response):
        # Logic after logging in: parse response
        title = response.xpath('//title/text()').get()
        self.logger.info(f"Logged in page title is: {title}")
        
        # Continue with more parsing or returning items
        # Example:
        # item = MyItem()
        # item['title'] = title
        # yield item