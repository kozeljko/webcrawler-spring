package si.kozelj.webcrawler.services;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WebCrawlerFrontier {
    private Queue<String> siteQueue = new ConcurrentLinkedDeque<>();

    public String getNext() {
        return siteQueue.remove();
    }

    public void addNew(String newSite) {
//        if (siteQueue.contains(newSite)) {
//            return;
//        }

        siteQueue.add(newSite);
    }
}
