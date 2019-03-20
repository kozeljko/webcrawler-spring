package si.kozelj.webcrawler.services;


import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.repositories.PageRepository;

import java.util.UUID;

@Scope("prototype")
@Component
public class WebCrawlerWorker implements Runnable{

    @Autowired
    private PageRepository repository;

    @Autowired
    private WebCrawlerFrontier frontier;

    @Override
    public void run() {
        String id = UUID.randomUUID().toString();

        System.out.println(frontier == null ? "empty" : "cool");

        frontier.addNew(id.charAt(0) + "");
        frontier.addNew(id.charAt(0) + "");

        while (true) {
            System.out.println(id + " " + frontier.getNext());
            frontier.addNew(id.charAt(0) + "");
            frontier.addNew(id.charAt(0) + "");
            try {
                repository.saveAndFlush(new Page());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Can't sleep: " + id);
            }
        }
    }
}
