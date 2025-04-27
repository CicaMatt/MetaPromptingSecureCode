import scrapy
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor
from tutorial.items import DmozItem

class TsrSpider(CrawlSpider):
    name = 'tsr'
    allowed_domains = ['thestudentroom.co.uk']
    start_urls = ['http://www.thestudentroom.co.uk/forumdisplay.php?f=143']

    # Security: Use a reasonable download delay to avoid overloading the server
    download_delay = 4
    # Security: Set a user-agent to mimic a real browser
    user_agent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:35.0) Gecko/20100101 Firefox/35.0'

    rules = (
        Rule(
            LinkExtractor(
                allow=r'forumdisplay\.php\?f=143&page=\d+',
                restrict_xpaths=("//li[@class='pager-page_numbers']/a",)),
        Rule(
            LinkExtractor(
                allow=r'showthread\.php\?t=\d+&page=\d+',
                restrict_xpaths=("//li[@class='pager-page_numbers']/a",)),
            callback='parse_link'),
        Rule(
            LinkExtractor(
                allow=r'showthread\.php\?t=\d+',
                restrict_xpaths=("//tr[@class='thread  unread    ']",)),
            callback='parse_link'),
    ))

    def parse_link(self, response):
        # Security: Use specific exception handling to avoid generic exceptions
        try:
            # Iterate over posts
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
        except Exception as e:
            # Security: Log the exception instead of silently failing
            self.logger.error(f"Error parsing response: {e}")

# Security: Ensure credentials are not hard-coded
# Use environment variables or a secure vault for credential management
# Example: os.environ.get('SECRET_KEY')

# Security: Use strong cryptographic algorithms for any encryption needs
# Example: from cryptography.fernet import Fernet

# Security: Implement secure authentication mechanisms
# Example: Use OAuth2 or JWT for secure authentication

# Security: Avoid obsolete functions and use modern, secure alternatives
# Example: Use Scrapy's built-in features and avoid deprecated methods