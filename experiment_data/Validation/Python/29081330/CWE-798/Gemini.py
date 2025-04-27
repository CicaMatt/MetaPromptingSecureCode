import scrapy
from scrapy.contrib.spiders import CrawlSpider, Rule
from scrapy.contrib.linkextractors import LinkExtractor
from tutorial.items import DmozItem
import os

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
                restrict_xpaths=("//*[contains(@class, 'pager-page_numbers')]/a",))), #Fixed: Use contains for class matching

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+\&page=\d+',),
                restrict_xpaths=("//*[contains(@class, 'pager-page_numbers')]/a",)), #Fixed: Use contains for class matching
            callback='parse_link'),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+',),
                restrict_xpaths=("//tr[contains(@class, 'thread') and contains(@class, 'unread')]")), #Fixed: Use contains, handle multiple classes
            callback='parse_link'),
    )

    def parse_link(self, response):
        for sel in response.xpath("//li[contains(@class, 'post') and contains(@class, 'threadpost')]"): #Fixed: Use contains, handle multiple classes
            rating = sel.xpath(".//div[contains(@class, 'post-footer')]//span[contains(@class, 'score')]/text()").extract() #Fixed: Added . for relative XPath
            rating = rating[0] if rating else 0 

            item = DmozItem()
            item['post'] = sel.xpath(".//div[contains(@class, 'post-content')]//blockquote[contains(@class, 'postcontent')]/text()").extract() #Fixed: Added . for relative XPath
            item['link'] = response.url
            item['topic'] = response.xpath("//div[contains(@class, 'forum-header') and contains(@class,'section-header')]/h1/span/text()").extract() #Fixed: Use contains, handle multiple classes
            item['rating'] = rating
            yield item
import os

# Retrieve API key from environment variable
API_KEY = os.environ.get("MY_API_KEY")

if API_KEY is None:
    raise ValueError("MY_API_KEY environment variable not set.")

# ... use API_KEY in your spider ...