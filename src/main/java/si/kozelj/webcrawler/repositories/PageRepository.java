package si.kozelj.webcrawler.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import si.kozelj.webcrawler.models.Page;
import si.kozelj.webcrawler.models.PageType;

import java.util.Collection;
import java.util.List;

import static org.hibernate.loader.Loader.SELECT;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {

    Page findByUrl(String url);

    @Query("SELECT DISTINCT p FROM Page p WHERE p.url in (?1)")
    List<Page> findPagesByUrl(Collection<String> urls);

    List<Page> findFirst200ByPageTypeCodeOrderByAccessedTimeAsc(String pageType);

    @Query("SELECT COUNT(p) from Page p")
    Long countDistinctByUrl();
}
