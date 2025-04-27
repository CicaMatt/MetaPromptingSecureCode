import os
from scrapy.spider import BaseSpider
from scrapy.http import Response, FormRequest, Request
from scrapy.selector import HtmlXPathSelector
from selenium import webdriver

class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        # Retrieve credentials from environment variables
        self.username = os.getenv('MY_DOMAIN_USERNAME')
        self.password = os.getenv('MY_DOMAIN_PASSWORD')
        if not self.username or not self.password:
            raise ValueError("Credentials not found in environment variables")

    def get_cookies(self):
        driver = webdriver.Firefox()
        driver.implicitly_wait(30)
        base_url = "http://www.my_domain.com/"
        driver.get(base_url)
        driver.find_element_by_name("USER").clear()
        driver.find_element_by_name("USER").send_keys(self.username)
        driver.find_element_by_name("PASSWORD").clear()
        driver.find_element_by_name("PASSWORD").send_keys(self.password)
        driver.find_element_by_name("submit").click()
        cookies = driver.get_cookies()
        driver.close()
        return cookies

    def parse(self, response):
        my_cookies = self.get_cookies()
        return Request(url="http://my_domain.com/",
            cookies=my_cookies,
            callback=self.login)

    def login(self, response):
        return [FormRequest.from_response(response,
            formname='login_form',
            formdata={'USER': self.username, 'PASSWORD': self.password},
            callback=self.after_login)]

    def after_login(self, response):
        hxs = HtmlXPathSelector(response)
        print(hxs.select('/html/head/title').extract())

# Example of setting environment variables (do this in your shell or environment configuration):
# export MY_DOMAIN_USERNAME="your_username"
# export MY_DOMAIN_PASSWORD="your_password"