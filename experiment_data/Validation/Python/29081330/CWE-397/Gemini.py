import scrapy

from tutorial.items import DmozItem
from scrapy.contrib.spiders import CrawlSpider, Rule
from scrapy.contrib.linkextractors import LinkExtractor
from scrapy.selector import Selector

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
                restrict_xpaths=("//*[@class='pager-page_numbers']/a",)), #Corrected XPath and removed @href
            ),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+\&page=\d+',),
                restrict_xpaths=("//*[@class='pager-page_numbers']/a",)), #Corrected XPath and removed @href
            callback='parse_link'),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+',),
                restrict_xpaths=("//*[@class='thread  unread    ']",)), #Corrected XPath
            callback='parse_link'),
        )

    def parse_link(self, response):
        # Iterate over posts.  Wrap in try-except for potential errors
        try:
            for sel in response.xpath("//li[@class='post threadpost old   ']"):
                rating = sel.xpath("div[@class='post-footer']//span[@class='score']/text()").extract()
                if not rating:
                    rating = 0
                else:
                    rating = rating[0]
                item = DmozItem()
                item['post'] = sel.xpath("div[@class='post-content']/blockquote[@class='postcontent restore']/text()").extract()
                item['link'] = response.url
                item['topic'] = response.xpath("//div[@class='forum-header section-header']/h1/span/text()").extract()
                item['rating'] = rating
                yield item
        except Exception as e: # Catch specific exceptions if possible (e.g., AttributeError)
            print(f"Error parsing page: {response.url}")
            print(f"Exception: {e}")