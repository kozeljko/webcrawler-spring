package si.kozelj.webcrawler.services.singletons;

import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import si.kozelj.webcrawler.CrawlerConstants;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.repositories.PageRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SiteFrontierHandler {
    private final Queue<String> siteQueue = new ConcurrentLinkedDeque<>();
    private final Logger logger = LoggerFactory.getLogger(SiteFrontierHandler.class);
    private boolean addMorePages = true;

    @Autowired
    private PageRepository pageRepository;

    @PostConstruct
    public void postConstruct() {
        addMorePages = !hasHundredKPages();
    }

    public synchronized String getNext() {
//        logger.info("Size is"  + siteQueue.size());
        if (siteQueue.isEmpty()) {
            siteQueue.addAll(getNewFrontier());
            logger.info("Added " + siteQueue.size() + " items to queue.");

            if (hasHundredKPages()) {
                logger.info("Disallowing any new entries into the page table.");
                addMorePages = false;
            }
        }

        return siteQueue.poll();
    }

    public boolean isAddMorePages() {
        return addMorePages;
    }

    private List<String> getNewFrontier() {
        return StreamEx.of(pageRepository.findFirst200ByPageTypeCodeOrderByAccessedTimeAsc(CrawlerConstants.PAGE_TYPE_FRONTIER)).map(Page::getUrl).toList();
    }

    private boolean hasHundredKPages() {
        return pageRepository.countDistinctByUrl() > 100_000L;
    }
}
