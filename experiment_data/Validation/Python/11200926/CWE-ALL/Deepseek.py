import os
from scrapy.spider import BaseSpider
from scrapy.http import FormRequest, Request
from scrapy.selector import HtmlXPathSelector
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
from selenium.common.exceptions import TimeoutException, NoSuchElementException

class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        # Load credentials from environment variables to avoid hard-coding
        self.username = os.getenv('MY_USERNAME')
        self.password = os.getenv('MY_PASSWORD')
        if not self.username or not self.password:
            raise ValueError("Credentials not found in environment variables.")

    def get_cookies(self):
        """
        Use Selenium to log in and retrieve cookies.
        """
        try:
            # Configure Selenium to run in headless mode for better security and performance
            options = Options()
            options.headless = True
            driver = webdriver.Firefox(options=options)
            driver.implicitly_wait(30)
            base_url = "http://www.my_domain.com/"
            driver.get(base_url)

            # Locate and fill in the login form
            driver.find_element_by_name("USER").clear()
            driver.find_element_by_name("USER").send_keys(self.username)
            driver.find_element_by_name("PASSWORD").clear()
            driver.find_element_by_name("PASSWORD").send_keys(self.password)
            driver.find_element_by_name("submit").click()

            # Retrieve cookies after successful login
            cookies = driver.get_cookies()
            driver.quit()
            return cookies
        except (TimeoutException, NoSuchElementException) as e:
            # Handle specific exceptions for better error recovery
            print(f"Error during login: {e}")
            driver.quit()
            return None

    def parse(self, response):
        """
        Parse the initial response and pass cookies to the login request.
        """
        cookies = self.get_cookies()
        if not cookies:
            raise ValueError("Failed to retrieve cookies.")
        return Request(url="http://my_domain.com/",
                      cookies=cookies,
                      callback=self.login)

    def login(self, response):
        """
        Submit the login form using the retrieved cookies.
        """
        return FormRequest.from_response(response,
                                         formname='login_form',
                                         formdata={'USER': self.username, 'PASSWORD': self.password},
                                         callback=self.after_login)

    def after_login(self, response):
        """
        Handle the response after successful login.
        """
        hxs = HtmlXPathSelector(response)
        title = hxs.select('/html/head/title').extract()
        print(f"Page title after login: {title}")