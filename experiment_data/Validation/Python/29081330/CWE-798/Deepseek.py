import scrapy
from tutorial.items import DmozItem
from scrapy.contrib.spiders import CrawlSpider, Rule
from scrapy.contrib.linkextractors import LinkExtractor

class TsrSpider(CrawlSpider):
    name = 'tsr'
    allowed_domains = ['thestudentroom.co.uk']
    start_urls = ['http://www.thestudentroom.co.uk/forumdisplay.php?f=143']

    download_delay = 4
    user_agent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:35.0) Gecko/20100101 Firefox/35.0'

    rules = (
        Rule(
            LinkExtractor(
                allow=('forumdisplay\.php\?f=143\&page=\d',),
                restrict_xpaths=("//li[@class='pager-page_numbers']/a",)),  # Removed @href
        ),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+\&page=\d+',),
                restrict_xpaths=("//li[@class='pager-page_numbers']/a",)),  # Removed @href
            callback='parse_link'),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+',),
                restrict_xpaths=("//tr[@class='thread  unread    ']",)),
            callback='parse_link'),
        )

    def parse_link(self, response):
        # Iterate over posts.
        for sel in response.xpath("//li[@class='post threadpost old   ']"):
            rating = sel.xpath(
                "div[@class='post-footer']//span[@class='score']/text()").extract()
            if not rating:
                rating = 0
            else:
                rating = rating[0]
            item = DmozItem()
            item['post'] = sel.xpath(
                "div[@class='post-content']/blockquote[@class='postcontent restore']/text()").extract()
            item['link'] = response.url
            item['topic'] = response.xpath(
                "//div[@class='forum-header section-header']/h1/span/text()").extract()
            item['rating'] = rating
            yield item
import os
from getpass import getpass
from cryptography.fernet import Fernet

# Generate a key for encryption (store this securely)
key = Fernet.generate_key()
cipher_suite = Fernet(key)

# Function to encrypt credentials
def encrypt_credentials(credentials):
    return cipher_suite.encrypt(credentials.encode())

# Function to decrypt credentials
def decrypt_credentials(encrypted_credentials):
    return cipher_suite.decrypt(encrypted_credentials).decode()

# Store credentials securely (e.g., in environment variables)
def store_credentials():
    username = input("Enter username: ")
    password = getpass("Enter password: ")
    credentials = f"{username}:{password}"
    encrypted_credentials = encrypt_credentials(credentials)
    os.environ['ENCRYPTED_CREDENTIALS'] = encrypted_credentials.decode()

# Retrieve and decrypt credentials
def get_credentials():
    encrypted_credentials = os.environ.get('ENCRYPTED_CREDENTIALS')
    if encrypted_credentials:
        return decrypt_credentials(encrypted_credentials.encode())
    return None

# Example usage
store_credentials()
credentials = get_credentials()
print(f"Retrieved credentials: {credentials}")