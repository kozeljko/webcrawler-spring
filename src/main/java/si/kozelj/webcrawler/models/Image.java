package si.kozelj.webcrawler.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Image {
    private int id;
    private String filename;
    private String contentType;
    private byte[] data;
    private Timestamp accessedTime;

    private Page page;

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
    @Column(name = "filename")
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "content_type")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Basic
    @Column(name = "data")
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Basic
    @Column(name = "accessed_time")
    public Timestamp getAccessedTime() {
        return accessedTime;
    }

    public void setAccessedTime(Timestamp accessedTime) {
        this.accessedTime = accessedTime;
    }

    @ManyToOne
    @JoinColumn(name = "page_id")
    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id &&
                Objects.equals(filename, image.filename) &&
                Objects.equals(contentType, image.contentType) &&
                Arrays.equals(data, image.data) &&
                Objects.equals(accessedTime, image.accessedTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filename, contentType, accessedTime);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
