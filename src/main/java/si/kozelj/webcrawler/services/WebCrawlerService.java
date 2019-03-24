package si.kozelj.webcrawler.services;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.UnknownFormatException;
import one.util.streamex.StreamEx;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;
import si.kozelj.webcrawler.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class WebCrawlerService {
    private static final Set<String> SEED_URLS = Sets.newHashSet("evem.gov.si", "e-uprava.gov.si", "podatki.gov.si", "e-prostor.gov.si");

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private RobotRulesHandler robotRulesHandler;

    @Autowired
    private SiteFrontierHandler frontierHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void runThreads() {
        // add seed urls to frontier
        StreamEx.of(SEED_URLS).map(Util::prependHttp).forEach(frontierHandler::addNew);

        // retrieve robot rules for seed urls
        Set<SimpleRobotRules> robotRules = StreamEx.of(SEED_URLS).map(Util::prependHttp).map(robotRulesHandler::getRobotRules).toSet();

        System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--ignore-certificate-errors");

        WebDriver webDriver = new ChromeDriver(options);

        webDriver.get("https://podatki.gov.si/robots2.txt");

        Document doc = Jsoup.parse(webDriver.getPageSource(), "https://podatki.gov.si");

        for (int i = 0; i < 1; i++) {
            taskExecutor.execute(ctx.getBean(WebCrawlerWorker.class));
        }
    }
}
