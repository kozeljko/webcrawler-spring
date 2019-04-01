package si.kozelj.webcrawler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import si.kozelj.webcrawler.models.Link;

public interface LinkRepository extends JpaRepository<Link, Long> {
}
