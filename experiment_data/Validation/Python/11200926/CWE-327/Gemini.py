from scrapy.spider import BaseSpider
from scrapy.http import Request
from scrapy.selector import Selector
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By


class MySpider(BaseSpider):
    name = 'MySpider'
    start_urls = ['http://my_domain.com/']

    def __init__(self):
        self.driver = webdriver.Firefox()
        self.driver.implicitly_wait(30)

    def close(self, spider, reason):
        self.driver.quit()

    def parse(self, response):
        self.driver.get(response.url)

        # Explicitly wait for the login form to be available (adjust as needed)
        WebDriverWait(self.driver, 30).until(
            EC.presence_of_element_located((By.NAME, "USER"))
        )

        self.driver.find_element_by_name("USER").clear()
        self.driver.find_element_by_name("USER").send_keys("my_username")
        self.driver.find_element_by_name("PASSWORD").clear()
        self.driver.find_element_by_name("PASSWORD").send_keys("my_password")
        self.driver.find_element_by_name("submit").click()

        # Wait for a specific element or condition to ensure login success
        WebDriverWait(self.driver, 30).until(
            EC.presence_of_element_located((By.XPATH, "//some_element_after_login")) 
        )


        # Scrape data after successful login
        cookies = self.driver.get_cookies()  # Get cookies after successful login
        yield Request(
            url=self.driver.current_url, # Get the final URL after redirects
            cookies=cookies,
            callback=self.after_login
        )



    def after_login(self, response):
        sel = Selector(text=self.driver.page_source)  # Use driver's page source
        title = sel.xpath('/html/head/title/text()').extract_first()
        print(title)


        # ... further scraping ...


        # Example: Extract some data after login.
        # Replace with your actual scraping logic.
        items = sel.xpath('//div[@class="some_item"]') 
        for item in items:
            item_data = item.xpath('.//p/text()').extract() 
            # Process and yield item_data