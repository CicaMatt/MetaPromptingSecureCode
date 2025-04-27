from scrapy.spider import BaseSpider
from scrapy.http import Response, FormRequest, Request
from scrapy.selector import HtmlXPathSelector
from selenium import webdriver
from selenium.common.exceptions import TimeoutException, NoSuchElementException

class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def get_cookies(self):
        driver = webdriver.Firefox()
        driver.implicitly_wait(30)
        base_url = "http://www.my_domain.com/"
        driver.get(base_url)
        try:  # Handle potential exceptions during login
            driver.find_element_by_name("USER").clear()
            driver.find_element_by_name("USER").send_keys("my_username")
            driver.find_element_by_name("PASSWORD").clear()
            driver.find_element_by_name("PASSWORD").send_keys("my_password")
            driver.find_element_by_name("submit").click()
            cookies = driver.get_cookies()
            driver.close()
            return cookies
        except NoSuchElementException as e:
            driver.close()  # Close the driver even on error
            self.logger.error(f"Element not found: {e}")  # Use Scrapy's logger
            return None # or raise  # Re-raise or handle as appropriate
        except TimeoutException as e:
            driver.close()
            self.logger.error(f"Timeout occurred: {e}")
            return None # or raise
        except Exception as e: # Catch any other unexpected exceptions
            driver.close()
            self.logger.error(f"An unexpected error occurred during login: {e}")
            return None # or raise

    def parse(self, response):
        my_cookies = self.get_cookies()  # Call get_cookies correctly
        if my_cookies: # check if cookies were obtained successfully
            return Request(url="http://my_domain.com/",
                          cookies=my_cookies,
                          callback=self.login)
        else:
            self.logger.error("Failed to retrieve cookies. Skipping login.")
            return None # Or handle the failure differently, like retrying

    def login(self, response):
        return [FormRequest.from_response(response,
                                         formname='login_form',
                                         formdata={'USER': 'my_username', 'PASSWORD': 'my_password'},
                                         callback=self.after_login,
                                         errback=self.login_error)] # Add errback

    def login_error(self, failure):
        self.logger.error(repr(failure))


    def after_login(self, response):
        hxs = HtmlXPathSelector(response)
        print(hxs.select('/html/head/title').extract())