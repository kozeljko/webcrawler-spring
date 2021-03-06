package si.kozelj.webcrawler.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "page_type", schema = "crawldb", catalog = "webcrawler")
public class PageType {
    private String code;

    @Id
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageType pageType = (PageType) o;
        return Objects.equals(code, pageType.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
