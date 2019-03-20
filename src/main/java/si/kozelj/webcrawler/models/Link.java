package si.kozelj.webcrawler.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Objects;

@Entity
@IdClass(LinkPK.class)
public class Link {
    private int fromPage;
    private int toPage;

    @Id
    @Column(name = "from_page")
    public int getFromPage() {
        return fromPage;
    }

    public void setFromPage(int fromPage) {
        this.fromPage = fromPage;
    }

    @Id
    @Column(name = "to_page")
    public int getToPage() {
        return toPage;
    }

    public void setToPage(int toPage) {
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
