package si.kozelj.webcrawler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("worker");
        executor.setQueueCapacity(210);
        executor.initialize();

        return executor;
    }
}
