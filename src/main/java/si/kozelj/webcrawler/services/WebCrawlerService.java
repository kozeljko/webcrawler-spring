package si.kozelj.webcrawler.services;

import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import si.kozelj.webcrawler.CrawlerConstants;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.repositories.PageRepository;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.util.Util;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class WebCrawlerService {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private RobotRulesHandler robotRulesHandler;

    @Autowired
    private PageRepository pageRepository;

    private final Logger logger = LoggerFactory.getLogger(WebCrawlerService.class);

    @EventListener(ApplicationReadyEvent.class)
    public void runThreads() {

        if (pageRepository.countDistinctByUrl() == 0L) {
            // add seed urls to frontier
            logger.info("Putting seed urls in frontier");
            Timestamp timestamp = Timestamp.from(Instant.now());
            StreamEx.of(CrawlerConstants.SEED_URLS).map(Util::prependHttp).forEach(url -> {
                Page page = new Page();
                page.setAccessedTime(timestamp);
                page.setPageTypeCode(CrawlerConstants.PAGE_TYPE_FRONTIER);
                page.setUrl(url);

                try {
                    pageRepository.saveAndFlush(page);
                } catch (Exception ignored) {

                }
            });

            // retrieve robot rules for seed urls
            logger.info("Retrieving robot rules for seed urls");
            StreamEx.of(CrawlerConstants.SEED_URLS).map(Util::prependHttp).forEach(robotRulesHandler::getRobotRules);
        }

        for (int i = 0; i < 216; i++) {
            taskExecutor.execute(ctx.getBean(WebCrawlerWorker.class));
        }
    }
}
