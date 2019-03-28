package si.kozelj.webcrawler.repositories;

import org.springframework.data.repository.CrudRepository;
import si.kozelj.webcrawler.models.PageType;

public interface PageTypeRepository extends CrudRepository<PageType, String> {

    PageType getByCode(String code);

    default PageType getHtmlType() {
        return getByCode("HTML");
    }

    default PageType getBinaryType() {
        return getByCode("BINARY");
    }

    default PageType getDuplicateType() {
        return getByCode("DUPLICATE");
    }

    default PageType getFrontierType() {
        return getByCode("FRONTIER");
    }

}
