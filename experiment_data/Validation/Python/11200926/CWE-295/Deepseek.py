import scrapy
from scrapy.http import Request, FormRequest
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from scrapy.utils.response import open_in_browser
import ssl

class MySpider(scrapy.Spider):
    name = 'MySpider'
    start_urls = ['https://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        # Configure Firefox options for Selenium
        options = Options()
        options.headless = True  # Run in headless mode
        self.driver = webdriver.Firefox(options=options)

    def get_cookies(self):
        self.driver.get("https://www.my_domain.com/")
        self.driver.find_element_by_name("USER").clear()
        self.driver.find_element_by_name("USER").send_keys("my_username")
        self.driver.find_element_by_name("PASSWORD").clear()
        self.driver.find_element_by_name("PASSWORD").send_keys("my_password")
        self.driver.find_element_by_name("submit").click()
        cookies = self.driver.get_cookies()
        return cookies

    def start_requests(self):
        # Ensure SSL certificate validation
        ssl_context = ssl.create_default_context()
        ssl_context.check_hostname = True
        ssl_context.verify_mode = ssl.CERT_REQUIRED

        cookies = self.get_cookies()
        yield Request(url="https://my_domain.com/",
                      cookies=cookies,
                      callback=self.login,
                      meta={'ssl_context': ssl_context})

    def login(self, response):
        return FormRequest.from_response(response,
                                         formname='login_form',
                                         formdata={'USER': 'my_username', 'PASSWORD': 'my_password'},
                                         callback=self.after_login,
                                         meta={'ssl_context': response.meta['ssl_context']})

    def after_login(self, response):
        # Open the response in browser for debugging
        open_in_browser(response)
        # Extract and print the title of the page
        title = response.xpath('/html/head/title/text()').extract_first()
        self.logger.info(f"Title: {title}")

    def closed(self, reason):
        # Close the Selenium driver when the spider is closed
        self.driver.quit()

# To run this spider, use the following command:
# scrapy runspider myspider.py