package si.kozelj.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.repositories.PageRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@SpringBootApplication
public class WebcrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebcrawlerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(PageRepository repository) {
        return (args) -> {

            System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");



            WebDriver webDriver = new ChromeDriver(options);

            webDriver.get("http://evem.gov.si");

            Document doc = Jsoup.parse(webDriver.getPageSource(), "http://www.google.si");

            Page page = new Page();
            page.setHtmlContent("content");
            page.setHttpStatusCode(200);
            page.setUrl("sa3d243");
            page.setAccessedTime(Timestamp.from(Instant.now()));
            repository.save(page);
            System.out.println("Saving");

            for (Page page2 : repository.findAll()) {
                System.out.println("Found ");
            }
        };
    }
}
