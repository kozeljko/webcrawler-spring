package si.kozelj.webcrawler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;


@Configuration
public class AppConfig {

    @Value("${worker.threads.number}")
    private Integer threadNumber;

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
        executor.setCorePoolSize(threadNumber);
        executor.setMaxPoolSize(threadNumber);
        executor.setThreadNamePrefix("worker");
        executor.setQueueCapacity(threadNumber + 200);
        executor.initialize();

        return executor;
    }
}
