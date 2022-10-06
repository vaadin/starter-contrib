package my.app.data.entity;

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

@Entity
public class SampleBook extends AbstractEntity {

    @Nonnull
    private String name;
    @Nonnull
    private String author;
    private LocalDate publicationDate;
    @Nonnull
    private Integer pages;
    @Nonnull
    private String isbn;
    @Type(type = "uuid-char")
    private UUID imageId;

    public UUID getImageId() {
        return imageId;
    }

    public void setImage(UUID imageId) {
        this.imageId = imageId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

}
