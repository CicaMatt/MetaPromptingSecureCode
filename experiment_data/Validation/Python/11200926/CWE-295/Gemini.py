from scrapy.spider import BaseSpider
from scrapy.http import Request
from scrapy.selector import Selector
from selenium import webdriver

class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']  # Replace with your actual domain

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        self.driver = webdriver.Firefox() # Or webdriver.Chrome() etc.
        self.driver.implicitly_wait(30)


    def get_cookies(self, response):
        self.driver.get(response.url)
        self.driver.find_element_by_name("USER").clear()
        self.driver.find_element_by_name("USER").send_keys("my_username") # Replace with your username
        self.driver.find_element_by_name("PASSWORD").clear()
        self.driver.find_element_by_name("PASSWORD").send_keys("my_password")  # Replace with your password
        self.driver.find_element_by_name("submit").click() # Replace 'submit' with the name of your submit button.
        cookies = self.driver.get_cookies()
        return cookies


    def parse(self, response):
        return Request(url=response.url,
                       cookies=self.get_cookies(response),
                       callback=self.after_login,
                       dont_filter=True)  # Crucial to avoid filtering based on URL



    def after_login(self, response):
        # Check for successful login (e.g., presence of a specific element)
        sel = Selector(response)

        # Example: checking for a welcome message or a specific element after login
        success_element = sel.xpath("//div[@id='welcome-message']")  # Replace with your success indicator XPath

        if success_element:
            print("Login Successful!")
            # Proceed with scraping...
            # Example: extract title
            title = sel.xpath("//title/text()").get()
            print(f"Title: {title}")



        else:
            print("Login Failed!")
            # Handle login failure (e.g., retry, report error)


    def closed(self, reason):
        self.driver.quit()