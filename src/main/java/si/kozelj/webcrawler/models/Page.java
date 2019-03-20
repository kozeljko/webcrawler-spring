package si.kozelj.webcrawler.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Page {
    private int id;
    private String url;
    private String htmlContent;
    private Integer httpStatusCode;
    private Timestamp accessedTime;

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
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "html_content")
    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    @Basic
    @Column(name = "http_status_code")
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Basic
    @Column(name = "accessed_time")
    public Timestamp getAccessedTime() {
        return accessedTime;
    }

    public void setAccessedTime(Timestamp accessedTime) {
        this.accessedTime = accessedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return id == page.id &&
                Objects.equals(url, page.url) &&
                Objects.equals(htmlContent, page.htmlContent) &&
                Objects.equals(httpStatusCode, page.httpStatusCode) &&
                Objects.equals(accessedTime, page.accessedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, htmlContent, httpStatusCode, accessedTime);
    }
}
