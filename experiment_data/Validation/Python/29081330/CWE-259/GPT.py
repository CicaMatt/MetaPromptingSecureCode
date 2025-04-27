import scrapy
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor
from myproject.items import DmozItem  # Ensure your project's item model is correctly referred

class TsrSpider(CrawlSpider):
    name = 'tsr'
    allowed_domains = ['thestudentroom.co.uk']
    start_urls = ['http://www.thestudentroom.co.uk/forumdisplay.php?f=143']

    download_delay = 4
    user_agent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:35.0) Gecko/20100101 Firefox/35.0'

    rules = (
        Rule(
            LinkExtractor(
                allow=(r'forumdisplay\.php\?f=143\&page=\d+',),
                restrict_xpaths=("//*[contains(@class, 'pager-page_numbers')]//a[@href]",)
            ),
            follow=True
        ),
        Rule(
            LinkExtractor(
                allow=(r'showthread\.php\?t=\d+\&page=\d+',),
                restrict_xpaths=("//*[contains(@class, 'pager-page_numbers')]//a[@href]",)
            ),
            callback='parse_link'
        ),
        Rule(
            LinkExtractor(
                allow=(r'showthread\.php\?t=\d+',),
                restrict_xpaths=("//*[contains(@class, 'thread unread')]",)
            ),
            callback='parse_link'
        ),
    )

    def parse_link(self, response):
        for sel in response.xpath("//li[contains(@class, 'post threadpost old')]"):
            rating = sel.xpath("div[contains(@class, 'post-footer')]//span[contains(@class, 'score')]/text()").get() or 0
            item = DmozItem(
                post=sel.xpath("div[contains(@class, 'post-content')]/blockquote[contains(@class, 'postcontent restore')]/text()").getall(),
                link=response.url,
                topic=response.xpath("//div[contains(@class, 'forum-header section-header')]/h1/span/text()").get(),
                rating=rating,
            )
            yield item