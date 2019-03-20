package si.kozelj.webcrawler.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import si.kozelj.webcrawler.models.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {
}
