package si.kozelj.webcrawler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import si.kozelj.webcrawler.models.Page;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    Page findByUrl(String url);

}
