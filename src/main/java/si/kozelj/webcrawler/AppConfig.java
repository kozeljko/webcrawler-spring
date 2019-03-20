package si.kozelj.webcrawler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import si.kozelj.webcrawler.services.WebCrawlerFrontier;

@Configuration
public class AppConfig {

    @Bean
    @Scope("singleton")
    public WebCrawlerFrontier getFrontier() {
        return new WebCrawlerFrontier();
    }
}
