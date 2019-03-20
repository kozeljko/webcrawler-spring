package si.kozelj.webcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebcrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebcrawlerApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner demo(PageRepository repository) {
//        return (args) -> {
//
//            System.setProperty("webdriver.chrome.driver", "D:\\Libraries\\Documents\\Projects\\webcrawlerSpring\\driver\\chromedriver.exe");
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
//
//
//
//            WebDriver webDriver = new ChromeDriver(options);
//
//            webDriver.get("http://evem.gov.si");
//
//            Document doc = Jsoup.parse(webDriver.getPageSource(), "http://www.google.si");
//
//            Page page = new Page();
//            page.setHtmlContent("content");
//            page.setHttpStatusCode(200);
//            page.setUrl("sa3d243");
//            page.setAccessedTime(Timestamp.from(Instant.now()));
//            repository.save(page);
//            System.out.println("Saving");
//
//            for (Page page2 : repository.findAll()) {
//                System.out.println("Found ");
//            }
//        };
//    }
}
