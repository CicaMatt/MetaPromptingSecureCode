import os
from scrapy.spider import BaseSpider
from scrapy.http import Response, FormRequest, Request
from scrapy.selector import HtmlXPathSelector
from selenium import webdriver

class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def get_cookies(self):
        # Retrieve credentials from environment variables
        username = os.getenv('MY_USERNAME')
        password = os.getenv('MY_PASSWORD')

        if not username or not password:
            raise ValueError("Username or password not set in environment variables")

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
        driver.close()
        return cookies

    def parse(self, response):
        my_cookies = self.get_cookies()
        return Request(url="http://my_domain.com/",
            cookies=my_cookies,
            callback=self.login)

    def login(self, response):
        # Retrieve credentials from environment variables
        username = os.getenv('MY_USERNAME')
        password = os.getenv('MY_PASSWORD')

        if not username or not password:
            raise ValueError("Username or password not set in environment variables")

        return [FormRequest.from_response(response,
            formname='login_form',
            formdata={'USER': username, 'PASSWORD': password},
            callback=self.after_login)]

    def after_login(self, response):
        hxs = HtmlXPathSelector(response)
        print(hxs.select('/html/head/title').extract())