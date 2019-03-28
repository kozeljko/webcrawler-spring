package si.kozelj.webcrawler.services;

import com.google.common.collect.Sets;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.models.PageData;
import si.kozelj.webcrawler.models.Site;
import si.kozelj.webcrawler.repositories.*;
import si.kozelj.webcrawler.services.singletons.RobotRulesHandler;
import si.kozelj.webcrawler.services.singletons.SiteFrontierHandler;
import si.kozelj.webcrawler.util.Util;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class WebCrawlerService {
    private static final Set<String> SEED_URLS = Sets.newHashSet("evem.gov.si", "e-uprava.gov.si", "podatki.gov.si", "e-prostor.gov.si");

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private RobotRulesHandler robotRulesHandler;

    @Autowired
    private SiteFrontierHandler frontierHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void runThreads() {

        // add seed urls to frontier
        StreamEx.of(SEED_URLS).map(Util::prependHttp).forEach(frontierHandler::addNew);

        // retrieve robot rules for seed urls
        StreamEx.of(SEED_URLS).map(Util::prependHttp).forEach(robotRulesHandler::getRobotRules);

        for (int i = 0; i < 10; i++) {
            taskExecutor.execute(ctx.getBean(WebCrawlerWorker.class));
        }

    }
}
