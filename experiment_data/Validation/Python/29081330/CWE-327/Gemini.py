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
                restrict_xpaths=("//li[@class='pager-page_numbers']/a")), # Removed @href
            ),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+\&page=\d+',),
                restrict_xpaths=("//li[@class='pager-page_numbers']/a")), # Removed @href
            callback='parse_link'
        ),

        Rule(
            LinkExtractor(
                allow=('showthread\.php\?t=\d+',),
                restrict_xpaths=("//tr[@class='thread  unread    ']/td[2]/a")), # Added /td[2]/a
            callback='parse_link'
            ),
        )

    def parse_link(self, response):
#           Iterate over posts.     
        for sel in response.xpath("//li[contains(@class, 'post threadpost')]"): # More robust selector
            rating = sel.xpath(
            "div[@class='post-footer']//span[@class='score']/text()").extract()
            rating = rating[0] if rating else 0 # Simplified rating extraction
            item = DmozItem()
            item['post'] = sel.xpath(
    "div[@class='post-content']/blockquote[contains(@class,'postcontent')]/text()").extract() # More robust selector
            item['link'] = response.url
            item['topic'] = response.xpath(
    "//div[contains(@class,'forum-header')]/h1/span/text()").extract() # More robust selector
            item['rating'] = rating
            yield item