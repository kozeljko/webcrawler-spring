package si.kozelj.webcrawler.services.singletons;

import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.*;
import one.util.streamex.StreamEx;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import si.kozelj.webcrawler.util.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RobotRulesHandler {
    private Map<String, SimpleRobotRules> robotRulesByUrl = new HashMap<>();

    @Autowired
    private SiteFrontierHandler frontierHandler;

    public SimpleRobotRules getRobotRules(String url) {
        String targetUrl;
        try {
            URL urlInstance = new URL(new URL(url), "/robots.txt");
            targetUrl = urlInstance.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
        }

        if (robotRulesByUrl.containsKey(targetUrl)) {
            return robotRulesByUrl.get(targetUrl);
        }

        SimpleRobotRules newRobotRules = loadRobotRules(targetUrl);
        robotRulesByUrl.put(targetUrl, newRobotRules);

        return newRobotRules;
    }

    private SimpleRobotRules loadRobotRules(String targetUrl) {
        System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--ignore-certificate-errors");

        // retrieve source
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.get(targetUrl);

        // try to parse source
        SimpleRobotRules robotRules;
        try {
            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            robotRules = parser.parseContent(targetUrl, webDriver.getPageSource().getBytes(), "text/plain", "*");
        } catch (Exception e) {
            e.printStackTrace();
            robotRules = new SimpleRobotRules(SimpleRobotRules.RobotRulesMode.ALLOW_ALL);
        }

        // save the loaded page
        // TODO save the loaded page

        // handle sitemap
        SiteMapParser siteMapParser = new SiteMapParser();
        Set<String> candidates = new HashSet<>();
        for (String sitemapUrl : robotRules.getSitemaps()) {
            String correctedUrl = Util.prependHttp(sitemapUrl);
            webDriver.get(correctedUrl);

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

        // TODO check for uniqueness of these urls
        frontierHandler.addAll(candidates);

        return robotRules;
    }
}
