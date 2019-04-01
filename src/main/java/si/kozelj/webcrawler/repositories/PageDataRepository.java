package si.kozelj.webcrawler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import si.kozelj.webcrawler.models.PageData;

@Repository
public interface PageDataRepository extends JpaRepository<PageData, Long> {
}
