package si.kozelj.webcrawler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;

@Configuration
public class AppConfig {

    @Bean
    @Scope("singleton")
    public SiteFrontierHandler getFrontier() {
        return new SiteFrontierHandler();
    }

    @Bean
    @Scope("singleton")
    public RobotRulesHandler getRobotRules() {
        return new RobotRulesHandler();
    }
}
