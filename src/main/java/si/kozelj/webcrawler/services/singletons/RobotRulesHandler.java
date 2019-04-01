package si.kozelj.webcrawler.services.singletons;

import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import one.util.streamex.StreamEx;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import si.kozelj.webcrawler.CrawlerConstants;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.models.Site;
import si.kozelj.webcrawler.repositories.PageRepository;
import si.kozelj.webcrawler.repositories.SiteRepository;
import si.kozelj.webcrawler.util.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class RobotRulesHandler {
    private Map<String, SimpleRobotRules> robotRulesByUrl = new HashMap<>();

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private SiteRepository siteRepository;

    private final Logger logger = LoggerFactory.getLogger(RobotRulesHandler.class);

    public SimpleRobotRules getRobotRules(String url) {
        String targetUrl;
        String domainUrl;
        try {
            URL urlInstance = new URL(Util.prependHttp(url));
            domainUrl = urlInstance.getHost();
            if (robotRulesByUrl.containsKey(domainUrl)) {
                return robotRulesByUrl.get(domainUrl);
            }

            targetUrl = new URL(urlInstance, "/robots.txt").toString();
        } catch (MalformedURLException e) {
            return new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
        }

        SimpleRobotRules newRobotRules = loadRobotRules(domainUrl, targetUrl);
        robotRulesByUrl.put(domainUrl, newRobotRules);

        return newRobotRules;
    }

    private synchronized SimpleRobotRules loadRobotRules(String domainUrl, String targetUrl) {
        System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--ignore-certificate-errors");

        // retrieve source
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.get(targetUrl);

        Site site = new Site();
        site.setDomain(domainUrl);

        String robotsContent = webDriver.getPageSource();
        site.setRobotsContent(robotsContent);

        // try to parse source
        SimpleRobotRules robotRules;
        try {
            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            robotRules = parser.parseContent(targetUrl, robotsContent.getBytes(), "text/plain", "*");
        } catch (Exception e) {
            robotRules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
        }

        // handle sitemap
        SiteMapParser siteMapParser = new SiteMapParser();
        Set<String> candidates = new HashSet<>();
        for (String sitemapUrl : robotRules.getSitemaps()) {
            String correctedUrl = Util.prependHttp(sitemapUrl);
            webDriver.get(correctedUrl);

            site.setSitemapContent(webDriver.getPageSource());

            SiteMap siteMap;
            try {
                siteMap = (SiteMap) siteMapParser.parseSiteMap(webDriver.getPageSource().getBytes(), new URL(correctedUrl));
            } catch (Exception e) {
                continue;
            }

            List<String> siteMapURLS = StreamEx.of(siteMap.getSiteMapUrls()).map(SiteMapURL::getUrl).map(URL::toString).toList();

            candidates.addAll(siteMapURLS);
        }

        // create the new site object and add the content
        try {
            siteRepository.saveAndFlush(site);
            logger.info("Saved site " + domainUrl);
        } catch (Exception e) {
            logger.info("Site domain has already been saved. Skipping saving.");
            return robotRules;
        }

        if (candidates.isEmpty()) {
            return robotRules;
        }

        Timestamp timestamp = Timestamp.from(Instant.now());
        Set<String> existingThings = StreamEx.of(pageRepository.findPagesByUrl(candidates)).map(Page::getUrl).toSet();
        for (String candidate : candidates) {
            if (existingThings.contains(candidate)) {
                continue;
            }

            Page page = new Page();
            page.setAccessedTime(timestamp);
            page.setPageTypeCode(CrawlerConstants.PAGE_TYPE_FRONTIER);
            page.setUrl(candidate);

            try {
                pageRepository.saveAndFlush(page);
            } catch (Exception ignored) {
                logger.error("Error while saving sitemap page");
            }
        }

        return robotRules;
    }
}
