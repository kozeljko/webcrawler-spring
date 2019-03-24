package si.kozelj.webcrawler.services;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import si.kozelj.webcrawler.repositories.PageRepository;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;

import java.util.UUID;

@Scope("prototype")
@Component
public class WebCrawlerWorker implements Runnable{

    @Autowired
    private PageRepository repository;

    @Autowired
    private SiteFrontierHandler siteFrontierHandler;

    @Autowired
    private RobotRulesHandler robotRulesHandler;

    @Override
    public void run() {
        String id = UUID.randomUUID().toString();

        // setup webdriver
        System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--ignore-certificate-errors");
        WebDriver webDriver = new ChromeDriver(options);

        webDriver.get("https://podatki.gov.si/robots2.txt");

        Document doc = Jsoup.parse(webDriver.getPageSource(), "https://podatki.gov.si");

        while (true) {
            // take url

            // check if has been visited?

            // get robots object (create new one, if doesn't exist

            // get site-map and add new items to queue

            // retrieve content

            // extract urls

            // extract images

            // extract documents
        }
    }
}
