import scrapy
from scrapy.http import FormRequest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.firefox.service import Service
from webdriver_manager.firefox import GeckoDriverManager

class MySpider(scrapy.Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        self.cookies = self.get_cookies()

    def get_cookies(self):
        service = Service(GeckoDriverManager().install())
        driver = webdriver.Firefox(service=service)
        driver.implicitly_wait(30)
        base_url = "http://www.my_domain.com/"
        driver.get(base_url)
        
        login_user = driver.find_element(By.NAME, "USER")
        login_user.clear()
        login_user.send_keys("my_username")
        
        login_password = driver.find_element(By.NAME, "PASSWORD")
        login_password.clear()
        login_password.send_keys("my_password")
        
        driver.find_element(By.NAME, "submit").click()
        cookies = driver.get_cookies()
        driver.quit()
        return {cookie['name']: cookie['value'] for cookie in cookies}

    def start_requests(self):
        yield scrapy.Request(
            url="http://my_domain.com/",
            cookies=self.cookies,
            callback=self.parse
        )

    def parse(self, response):
        yield FormRequest.from_response(
            response,
            formname='login_form',
            formdata={'USER': 'my_username', 'PASSWORD': 'my_password'},
            callback=self.after_login
        )

    def after_login(self, response):
        title = response.xpath('/html/head/title/text()').get()
        self.log(f"Page title after login: {title}")