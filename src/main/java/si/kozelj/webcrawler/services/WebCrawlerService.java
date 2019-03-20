package si.kozelj.webcrawler.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class WebCrawlerService {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired private ApplicationContext ctx;

    @EventListener(ApplicationReadyEvent.class)
    public void runThreads() {
        WebCrawlerFrontier frontier = new WebCrawlerFrontier();

        for (int i = 0; i < 2; i++) {
            taskExecutor.execute(ctx.getBean(WebCrawlerWorker.class));
        }
    }
}
