package si.kozelj.webcrawler.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import si.kozelj.webcrawler.models.Site;

@Repository
public interface SiteRepository extends CrudRepository<Site, Long> {
}
