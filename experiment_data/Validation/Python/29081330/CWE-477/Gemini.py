import scrapy

from tutorial.items import DmozItem
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor

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
                restrict_xpaths=("//*[contains(@class, 'pager-page_numbers')]/a",))), # Corrected xpath and used contains for class matching

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+\&page=\d+',),
                restrict_xpaths=("//*[contains(@class, 'pager-page_numbers')]/a",)), # Corrected xpath and used contains for class matching
            callback='parse_link'),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+',),
                restrict_xpaths=("//*[contains(@class, 'thread') and contains(@class, 'unread')]")), # Corrected xpath and used contains for class matching
            callback='parse_link'),
        )

    def parse_link(self, response):
#           Iterate over posts.     
        for sel in response.xpath("//*[contains(@class, 'post') and contains(@class, 'threadpost')]"): # Corrected xpath and used contains for class matching
            rating = sel.xpath(
            ".//div[contains(@class, 'post-footer')]//span[contains(@class, 'score')]/text()").extract_first() # Added .// for relative paths and extract_first()
            rating = rating or 0 # Simplified rating assignment

            item = DmozItem()
            item['post'] = sel.xpath(
            ".//div[contains(@class, 'post-content')]//blockquote[contains(@class, 'postcontent')]/text()").extract() # Added .// for relative paths
            item['link'] = response.url
            item['topic'] = response.xpath(
            "//div[contains(@class, 'forum-header') and contains(@class, 'section-header')]/h1/span/text()").extract_first() # Added contains for more robust class selection and extract_first()
            item['rating'] = rating
            yield item