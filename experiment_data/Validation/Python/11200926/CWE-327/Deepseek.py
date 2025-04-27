from scrapy import Spider, Request
from scrapy.http import FormRequest
from selenium import webdriver

class MySpider(Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        self.cookies = self.get_cookies()

    def get_cookies(self):
        # Initialize the Selenium WebDriver
        driver = webdriver.Firefox()
        driver.implicitly_wait(30)
        base_url = "http://www.my_domain.com/"
        driver.get(base_url)

        # Perform login
        driver.find_element_by_name("USER").clear()
        driver.find_element_by_name("USER").send_keys("my_username")
        driver.find_element_by_name("PASSWORD").clear()
        driver.find_element_by_name("PASSWORD").send_keys("my_password")
        driver.find_element_by_name("submit").click()

        # Get cookies after login
        cookies = driver.get_cookies()
        driver.close()
        return cookies

    def start_requests(self):
        # Use the cookies obtained from Selenium in the initial request
        for url in self.start_urls:
            yield Request(url=url, cookies=self.cookies, callback=self.parse)

    def parse(self, response):
        # Check if login was successful by parsing the response
        if "Welcome" in response.text:
            self.log("Login successful!")
            # Proceed with further scraping
            yield from self.after_login(response)
        else:
            self.log("Login failed!")

    def after_login(self, response):
        # Example of parsing the page after login
        title = response.xpath('/html/head/title/text()').extract_first()
        self.log(f"Page title: {title}")
        # Add further parsing logic here

# To run the spider, use the following command in the terminal:
# scrapy runspider myspider.py