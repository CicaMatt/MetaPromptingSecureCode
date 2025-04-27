import scrapy
from scrapy.crawler import CrawlerProcess
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
import requests

class MySpider(scrapy.Spider):
    name = 'my_spider'
    start_urls = ['https://my_domain.com/']

    def get_cookies(self):
        options = Options()
        options.headless = True
        driver = webdriver.Firefox(options=options)
        try:
            driver.implicitly_wait(30)
            base_url = "https://www.my_domain.com/"
            driver.get(base_url)
            # Assuming you need to log in
            driver.find_element_by_name("USER").clear()
            driver.find_element_by_name("USER").send_keys("my_username")
            driver.find_element_by_name("PASSWORD").clear()
            driver.find_element_by_name("PASSWORD").send_keys("my_password")
            driver.find_element_by_name("submit").click()
            cookies = driver.get_cookies()
            return {cookie['name']: cookie['value'] for cookie in cookies}
        finally:
            driver.quit()

    def start_requests(self):
        cookies = self.get_cookies()
        for url in self.start_urls:
            yield scrapy.Request(url=url, cookies=cookies, callback=self.parse)

    def parse(self, response):
        title = response.xpath('//title/text()').get()
        self.log(f'Logged in: Page title is {title}')

def run_spider():
    process = CrawlerProcess(settings={
        "USER_AGENT": "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
        "DEFAULT_REQUEST_HEADERS": {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': 'en',
        },
    })
    
    process.crawl(MySpider)
    process.start()

# Ensuring secure certificate validation with requests
def verify_ssl(url):
    try:
        response = requests.get(url, verify=True)  # 'verify=True' is the default, but makes it explicit
        response.raise_for_status()
        print(f"SSL/TLS certificate verified successfully for {url}.")
    except requests.exceptions.SSLError as ssl_err:
        print(f"SSL error occurred: {ssl_err}")
    except requests.exceptions.RequestException as err:
        print(f"Request error occurred: {err}")

# Ensure to validate the certificate before running the Scrapy process
verify_ssl('https://www.my_domain.com/')
run_spider()