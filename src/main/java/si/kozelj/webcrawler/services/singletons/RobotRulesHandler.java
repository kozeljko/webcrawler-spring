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
import org.springframework.beans.factory.annotation.Autowired;
import si.kozelj.webcrawler.models.Site;
import si.kozelj.webcrawler.repositories.SiteRepository;
import si.kozelj.webcrawler.util.Util;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RobotRulesHandler {
    private Map<String, SimpleRobotRules> robotRulesByUrl = new HashMap<>();

    @Autowired
    private SiteFrontierHandler frontierHandler;

    @Autowired
    private SiteRepository siteRepository;

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
            e.printStackTrace();
            return new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
        }

        SimpleRobotRules newRobotRules = loadRobotRules(domainUrl, targetUrl);
        robotRulesByUrl.put(domainUrl, newRobotRules);

        return newRobotRules;
    }

    private SimpleRobotRules loadRobotRules(String domainUrl, String targetUrl) {
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
            e.printStackTrace();
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
                e.printStackTrace();
                continue;
            }

            List<String> siteMapURLS = StreamEx.of(siteMap.getSiteMapUrls()).map(SiteMapURL::getUrl).map(URL::toString).toList();

            candidates.addAll(siteMapURLS);
        }
        frontierHandler.addAll(candidates);

        // create the new site object and add the content
        siteRepository.saveAndFlush(site);

        return robotRules;
    }
}
