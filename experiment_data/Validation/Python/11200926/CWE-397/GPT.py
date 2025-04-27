import logging
from scrapy.spiders import Spider
from scrapy.http import FormRequest, Request
from selenium import webdriver
from selenium.common.exceptions import WebDriverException, NoSuchElementException

class MySpider(Spider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self, *args, **kwargs):
        super(MySpider, self).__init__(*args, **kwargs)
        # Configure logging
        self.logger.setLevel(logging.DEBUG)

    def get_cookies(self):
        try:
            driver = webdriver.Firefox()
            self.logger.debug('Initialized WebDriver.')

            driver.implicitly_wait(30)
            base_url = "http://www.my_domain.com/"
            driver.get(base_url)
            self.logger.debug(f'Navigated to {base_url}.')

            try:
                user_elem = driver.find_element_by_name("USER")
                user_elem.clear()
                user_elem.send_keys("my_username")
                self.logger.debug(f'Entered username.')

                pass_elem = driver.find_element_by_name("PASSWORD")
                pass_elem.clear()
                pass_elem.send_keys("my_password")
                self.logger.debug('Entered password.')

                submit_elem = driver.find_element_by_name("submit")
                submit_elem.click()
                self.logger.debug('Clicked submit button.')
            except NoSuchElementException as e:
                self.logger.error(f'Element not found: {e}')
                return []

            cookies = driver.get_cookies()
            self.logger.debug(f'Cookies obtained: {cookies}')
            return cookies
        except WebDriverException as e:
            self.logger.error(f'Web driver error: {e}')
            return []
        finally:
            driver.quit()
            self.logger.debug('WebDriver closed.')

    def start_requests(self):
        cookies = self.get_cookies()
        if cookies:
            yield Request(url=self.start_urls[0],
                          cookies=cookies,
                          callback=self.parse)
        else:
            self.logger.warning('No cookies obtained, aborting.')

    def parse(self, response):
        return FormRequest.from_response(response,
                                         formname='login_form',
                                         formdata={'USER': 'my_username', 'PASSWORD': 'my_password'},
                                         callback=self.after_login)

    def after_login(self, response):
        # Process the response after login
        self.logger.debug('Processing after login.')
        if "Login Successful" not in response.text:
            self.logger.error('Login failed.')
            return
        
        # Continue with further processing as needed
        self.logger.debug('Login successful.')
        # Example: scraping or other logic goes here