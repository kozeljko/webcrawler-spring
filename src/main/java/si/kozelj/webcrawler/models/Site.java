package si.kozelj.webcrawler.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Site {
    private int id;
    private String domain;
    private String robotsContent;
    private String sitemapContent;

    private List<Page> pages = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "domain")
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Basic
    @Column(name = "robots_content")
    public String getRobotsContent() {
        return robotsContent;
    }

    public void setRobotsContent(String robotsContent) {
        this.robotsContent = robotsContent;
    }

    @Basic
    @Column(name = "sitemap_content")
    public String getSitemapContent() {
        return sitemapContent;
    }

    public void setSitemapContent(String sitemapContent) {
        this.sitemapContent = sitemapContent;
    }

    @OneToMany
    @JoinColumn(name = "site_id")
    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return id == site.id &&
                Objects.equals(domain, site.domain) &&
                Objects.equals(robotsContent, site.robotsContent) &&
                Objects.equals(sitemapContent, site.sitemapContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, domain, robotsContent, sitemapContent);
    }
}
