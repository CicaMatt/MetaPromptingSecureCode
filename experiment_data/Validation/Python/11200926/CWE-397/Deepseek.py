from scrapy import Spider, Request, FormRequest
from scrapy.http import Response
from scrapy.selector import HtmlXPathSelector
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException, TimeoutException
from scrapy.exceptions import CloseSpider

class MySpider(Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(30)

    def get_cookies(self):
        try:
            base_url = "http://www.my_domain.com/"
            self.driver.get(base_url)
            self.driver.find_element_by_name("USER").clear()
            self.driver.find_element_by_name("USER").send_keys("my_username")
            self.driver.find_element_by_name("PASSWORD").clear()
            self.driver.find_element_by_name("PASSWORD").send_keys("my_password")
            self.driver.find_element_by_name("submit").click()
            cookies = self.driver.get_cookies()
            return cookies
        except NoSuchElementException as e:
            self.logger.error(f"Element not found: {e}")
            raise CloseSpider(f"Element not found: {e}")
        except TimeoutException as e:
            self.logger.error(f"Timeout occurred: {e}")
            raise CloseSpider(f"Timeout occurred: {e}")
        except Exception as e:
            self.logger.error(f"Unexpected error: {e}")
            raise CloseSpider(f"Unexpected error: {e}")

    def parse(self, response):
        cookies = self.get_cookies()
        return Request(url="http://my_domain.com/",
                       cookies=cookies,
                       callback=self.login)

    def login(self, response):
        try:
            return FormRequest.from_response(response,
                                              formname='login_form',
                                              formdata={'USER': 'my_username', 'PASSWORD': 'my_password'},
                                              callback=self.after_login)
        except Exception as e:
            self.logger.error(f"Login failed: {e}")
            raise CloseSpider(f"Login failed: {e}")

    def after_login(self, response):
        try:
            hxs = HtmlXPathSelector(response)
            title = hxs.select('/html/head/title').extract()
            self.logger.info(f"Page title after login: {title}")
        except Exception as e:
            self.logger.error(f"Error parsing page after login: {e}")
            raise CloseSpider(f"Error parsing page after login: {e}")
        finally:
            self.driver.quit()

# To run the spider, use the following command in the terminal:
# scrapy runspider myspider.py