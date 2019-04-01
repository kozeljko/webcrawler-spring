package si.kozelj.webcrawler.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class LinkPK implements Serializable {
    private Long fromPage;
    private Long toPage;

    @Column(name = "from_page")
    @Id
    public Long getFromPage() {
        return fromPage;
    }

    public void setFromPage(Long fromPage) {
        this.fromPage = fromPage;
    }

    @Column(name = "to_page")
    @Id
    public Long getToPage() {
        return toPage;
    }

    public void setToPage(Long toPage) {
        this.toPage = toPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkPK that = (LinkPK) o;
        return fromPage == that.fromPage &&
                toPage == that.toPage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromPage, toPage);
    }
}
