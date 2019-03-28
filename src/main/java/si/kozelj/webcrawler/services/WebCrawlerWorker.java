package si.kozelj.webcrawler.services;


import com.google.common.base.Strings;
import crawlercommons.robots.SimpleRobotRules;
import one.util.streamex.StreamEx;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.models.Site;
import si.kozelj.webcrawler.repositories.PageRepository;
import si.kozelj.webcrawler.repositories.SiteRepository;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;
import si.kozelj.webcrawler.util.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Scope("prototype")
@Component
public class WebCrawlerWorker implements Runnable{

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private SiteFrontierHandler frontierHandler;

    @Autowired
    private RobotRulesHandler robotRulesHandler;

    private String threadID;

    @Override
    public void run() {
        threadID = UUID.randomUUID().toString();

        // setup webdriver
        System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--ignore-certificate-errors");
        WebDriver webDriver = new ChromeDriver(options);
        webDriver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

        Set<String> collected = new HashSet<>();
        for (String url; (url = frontierHandler.getNext()) != null;) {
            URL urlObject;
            try {
                urlObject = new URL(url);
            } catch (MalformedURLException e) {
//                e.printStackTrace();
                continue;
            }

            // check if has been visited? TODO maybe?

            // get robots object
            SimpleRobotRules robotRules = robotRulesHandler.getRobotRules(urlObject.toString());

            if (!robotRules.isAllowed(urlObject.toString())) {
                continue;
            }

            // TODO do delay

            // retrieve content
            String domainUrl = Util.extractDomainPart(urlObject);
            String correctedString = Util.prependHttp(urlObject.toString());
            System.out.println(threadID + ": Loading page: " + correctedString);
            webDriver.get(correctedString);

            // extract urls
            Document doc = Jsoup.parse(webDriver.getPageSource(), correctedString);

            collected.addAll(extractLinkTagItems(doc, webDriver.getCurrentUrl()));

            // extract images

            // extract documents


//            frontierHandler.addAll(collected);

            Site loadedSite = siteRepository.findSiteByDomain(domainUrl);

            Page newPage = new Page();
            newPage.setAccessedTime(Timestamp.from(Instant.now()));
            newPage.setUrl(webDriver.getCurrentUrl());
            newPage.setHtmlContent(webDriver.getPageSource());
            newPage.setHttpStatusCode(200);
            newPage.setSite(loadedSite);

            System.out.println("Saving");
            pageRepository.saveAndFlush(newPage);
            System.out.println("Saved");
        }

        System.out.println("Is empty");
    }

    private Set<String> extractLinkTagItems(Document document, String url) {
        Set<String> hrefs = StreamEx.of(document.getElementsByTag("a")).map(o -> o.attr("href")).toSet();
        Set<String> result = new HashSet<>();

        for (String target : hrefs) {
            if (target.startsWith("#") || target.startsWith("javascript")) {
                continue;
            }

            try {
                URL urlObject = new URL(new URL(url), target);
                if (!urlObject.getHost().endsWith(".gov.si")) {
                    continue;
                }

                result.add(urlObject.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private Set<String> extractOnClickITems(Document document, String url) {
        Set<String> result = new HashSet<>();

        return result;
    }

    private Set<String> extractImageItems(Document document, String url) {


        return new HashSet<>();
    }
}
