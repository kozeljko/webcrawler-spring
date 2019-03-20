package si.kozelj.webcrawler.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import si.kozelj.webcrawler.models.PageData;

@Repository
public interface PageDataRepository extends CrudRepository<PageData, Long> {
}
