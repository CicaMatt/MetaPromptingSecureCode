import scrapy
from scrapy.http import FormRequest, Request
from selenium import webdriver

class MySpider(scrapy.Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def get_cookies(self):
        driver = webdriver.Firefox()
        driver.implicitly_wait(30)
        base_url = "http://www.my_domain.com/"
        driver.get(base_url)
        driver.find_element_by_name("USER").clear()
        driver.find_element_by_name("USER").send_keys("my_username")
        driver.find_element_by_name("PASSWORD").clear()
        driver.find_element_by_name("PASSWORD").send_keys("my_password")
        driver.find_element_by_name("submit").click()
        cookies = driver.get_cookies()
        driver.close()
        return cookies

    def start_requests(self):
        cookies = self.get_cookies()
        yield Request(url="http://my_domain.com/",
                       cookies=cookies,
                       callback=self.login)

    def login(self, response):
        return FormRequest.from_response(response,
                                         formname='login_form',
                                         formdata={'USER': 'my_username', 'PASSWORD': 'my_password'},
                                         callback=self.after_login)

    def after_login(self, response):
        title = response.xpath('/html/head/title/text()').get()
        print(title)
from shutil import which

SELENIUM_DRIVER_NAME = 'firefox'
SELENIUM_DRIVER_EXECUTABLE_PATH = which('geckodriver')
SELENIUM_DRIVER_ARGUMENTS = ['-headless']  # Optional: Run in headless mode

DOWNLOADER_MIDDLEWARES = {
    'scrapy_selenium.SeleniumMiddleware': 800,
}
import scrapy
from scrapy_selenium import SeleniumRequest

class MySpider(scrapy.Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def start_requests(self):
        yield SeleniumRequest(url="http://my_domain.com/", callback=self.parse)

    def parse(self, response):
        driver = response.request.meta['driver']
        driver.find_element_by_name("USER").clear()
        driver.find_element_by_name("USER").send_keys("my_username")
        driver.find_element_by_name("PASSWORD").clear()
        driver.find_element_by_name("PASSWORD").send_keys("my_password")
        driver.find_element_by_name("submit").click()
        cookies = driver.get_cookies()
        yield scrapy.Request(url="http://my_domain.com/",
                             cookies=cookies,
                             callback=self.after_login)

    def after_login(self, response):
        title = response.xpath('/html/head/title/text()').get()
        print(title)