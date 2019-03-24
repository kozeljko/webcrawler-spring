package si.kozelj.webcrawler.services.singletons;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SiteFrontierHandler {
    private Queue<String> siteQueue = new ConcurrentLinkedDeque<>();

    public String getNext() {
        // TODO check if correct method
        return siteQueue.remove();
    }
    public void addNew(String newSite) {
        siteQueue.add(newSite);
    }

    public void addAll(Collection<String> newSites) {
        siteQueue.addAll(newSites);
    }
}
