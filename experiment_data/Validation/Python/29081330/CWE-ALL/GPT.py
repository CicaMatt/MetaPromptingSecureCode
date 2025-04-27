import scrapy
from scrapy.spiders import CrawlSpider, Rule
from scrapy.linkextractors import LinkExtractor
from tutorial.items import DmozItem

class TsrSpider(CrawlSpider):
    name = 'tsr'
    allowed_domains = ['thestudentroom.co.uk']
    start_urls = ['http://www.thestudentroom.co.uk/forumdisplay.php?f=143']

    # Adding user-agent to mimic browser to avoid being blocked by sites
    custom_settings = {
        'USER_AGENT': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:35.0) Gecko/20100101 Firefox/35.0',
        'DOWNLOAD_DELAY': 4  # throtle requests not to be banned by server
    }

    rules = (
        Rule(
            LinkExtractor(
                allow=(r'forumdisplay\.php\?f=143&page=\d',),
                restrict_xpaths="//div[@class='pagenav']//a"),  # Corrected XPath for pagination links
            follow=True),

        Rule(
            LinkExtractor(
                allow=(r'showthread\.php\?t=\d+&page=\d+',),
                restrict_xpaths="//div[@class='pagenav']//a"),  # Corrected XPath for pagination links
            callback='parse_link',
            follow=True),

        Rule(
            LinkExtractor(
                allow=(r'showthread\.php\?t=\d+',),
                restrict_xpaths="//tr[contains(@class, 'thread ')]"),  # Adjust XPath to capture threads
            callback='parse_link',
            follow=True),
    )

    def parse_link(self, response):
        for sel in response.xpath("//li[contains(@class, 'post threadpost')]"):  # More robust XPath for posts
            rating = sel.xpath("div[contains(@class, 'post-footer')]//span[@class='score']/text()").get(default='0')

            item = DmozItem()
            item['post'] = sel.xpath("div[contains(@class, 'post-content')]/blockquote[@class='postcontent restore']/text()").getall()
            item['link'] = response.url
            item['topic'] = response.xpath("//div[@class='forum-header section-header']/h1/span/text()").get(default='').strip()
            item['rating'] = rating.strip()
            yield item

# Note: The provided security mitigation strategies, such as those dealing with passwords, certificates, and cryptography,
# are more applicable to backend systems or different contexts than a Scrapy spider.
# However, always ensure your scraping practices adhere to ethical standards and website terms of service.