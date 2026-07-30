package th.mfu.service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * What a book looks like ON THE WIRE.
 *
 * Three things to notice, because they are the reason the DTO pattern exists.
 *
 * 1. It is FLAT. The entity has a Category object; this has category_id and
 *    category_name. The browser gets what it needs in one message and does not
 *    have to make a second call - "reduce the number of method calls".
 *
 * 2. It is SMALLER. The entity also has a list of transactions. That list is
 *    not here, so it never travels, and there is no endless
 *    book -> category -> book loop to break with @JsonIgnore.
 *
 * 3. The names are DIFFERENT. The field is called year but the JSON says
 *    "publish-year". You can rename a JSON field without touching the database
 *    column, and rename a column without breaking any client.
 *
 * Every field is an object type (Integer, not int). A primitive cannot be null,
 * and the partial update in BookMapper works by skipping whatever is null.
 */
public class BookDTO {

    private Long id;
    private String title;
    private String author;

    @JsonProperty("publish-year")
    private Integer year;

    @JsonProperty("added-date")
    private LocalDate addedDate;

    @JsonProperty("category_id")
    private Long categoryId;

    /** Read-only extra: filled in from the entity, ignored on the way back. */
    @JsonProperty("category_name")
    private String categoryName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
