package si.kozelj.webcrawler.models;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class LinkPK implements Serializable {
    private int fromPage;
    private int toPage;

    @Column(name = "from_page")
    @Id
    public int getFromPage() {
        return fromPage;
    }

    public void setFromPage(int fromPage) {
        this.fromPage = fromPage;
    }

    @Column(name = "to_page")
    @Id
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
        LinkPK linkPK = (LinkPK) o;
        return fromPage == linkPK.fromPage &&
                toPage == linkPK.toPage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromPage, toPage);
    }
}
