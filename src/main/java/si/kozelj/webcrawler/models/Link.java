package si.kozelj.webcrawler.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "link", schema = "crawldb", catalog = "webcrawler")
@IdClass(LinkPK.class)
public class Link {
    private Long fromPage;
    private Long toPage;

    public Link() {
    }

    public Link(Page fromPage, Page toPage) {
        this.fromPage = (long) fromPage.getId();
        this.toPage = (long) toPage.getId();
    }

    @Id
    @Column(name = "from_page")
    public Long getFromPage() {
        return fromPage;
    }

    public void setFromPage(Long fromPage) {
        this.fromPage = fromPage;
    }

    @Id
    @Column(name = "to_page")
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
        Link link = (Link) o;
        return fromPage == link.fromPage &&
                toPage == link.toPage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromPage, toPage);
    }
}
