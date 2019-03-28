package si.kozelj.webcrawler.services.singletons;

import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SiteFrontierHandler {
    private Queue<String> siteQueue = new ConcurrentLinkedDeque<>();

    public synchronized String getNext() {
        // TODO check if correct method
        return siteQueue.poll();
    }
    public synchronized void addNew(String newSite) {
        siteQueue.add(newSite);
    }

    public synchronized void addAll(Collection<String> newSites) {
        siteQueue.addAll(newSites);
//        siteQueue.addAll(StreamEx.of(newSites).limit(5).toSet());
    }
}
