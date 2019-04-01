package si.kozelj.webcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebcrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebcrawlerApplication.class, args);
//        System.setProperty("webdriver.chrome.driver", "/home/aljaz/Projects/webcrawler-spring/driver/linux-driver");
    }
}
