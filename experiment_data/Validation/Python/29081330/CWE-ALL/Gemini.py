import scrapy
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor
from tutorial.items import DmozItem  # Assuming this exists

class TsrSpider(CrawlSpider):
    name = 'tsr'
    allowed_domains = ['thestudentroom.co.uk']
    start_urls = ['http://www.thestudentroom.co.uk/forumdisplay.php?f=143']

    download_delay = 4
    user_agent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:35.0) Gecko/20100101 Firefox/35.0'


    rules = (
        Rule(LinkExtractor(
                allow=(r'forumdisplay\.php\?f=143\&page=\d+'),
                restrict_xpaths=("//li[contains(@class, 'pager-page_numbers')]/a")),
             follow=True), # Explicitly set follow=True

        Rule(LinkExtractor(
                allow=(r'showthread\.php\?t=\d+\&page=\d+'),
                restrict_xpaths=("//li[contains(@class, 'pager-page_numbers')]/a")),
             callback='parse_link', follow=True),

        Rule(LinkExtractor(
                allow=(r'showthread\.php\?t=\d+'),
                restrict_xpaths=("//tr[contains(@class, 'thread') and contains(@class, 'unread')]")),
             callback='parse_link'),
        )

    def parse_link(self, response):
        try:  # Add try/except block for specific exceptions.
            for sel in response.xpath("//li[contains(@class, 'post') and contains(@class, 'threadpost') and contains(@class, 'old')]"):
                rating = sel.xpath(".//div[@class='post-footer']//span[@class='score']/text()").extract_first() # extract_first() handles empty lists gracefully
                rating = int(rating) if rating else 0 # Safer conversion to integer

                item = DmozItem()
                item['post'] = sel.xpath(".//div[@class='post-content']//blockquote[contains(@class, 'postcontent')]/text()").extract()
                item['link'] = response.url
                item['topic'] = response.xpath("//div[contains(@class, 'forum-header') and contains(@class, 'section-header')]/h1/span/text()").extract_first() # extract_first() is safer
                item['rating'] = rating
                yield item
        except (ValueError, TypeError, AttributeError) as e:  # Catch specific expected exceptions
            print(f"Error processing item in {response.url}: {e}")