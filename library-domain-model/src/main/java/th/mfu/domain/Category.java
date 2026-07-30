package th.mfu.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * A group of books, for example "Programming" or "Fiction".
 *
 * Notice what is NOT here any more: no @JsonProperty, no @JsonIgnore, no
 * @JsonSerialize. Those moved to CategoryDTO in the web service module.
 * This class now speaks to the database and to nothing else.
 */
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    // One category has many books. Book owns the link, in its "category" field.
    //
    // In the JPA sample this field needed @JsonIgnore, or Jackson would loop
    // forever: category -> books -> category -> books. It does not need it any
    // more, because this class is never turned into JSON. The DTO decides what
    // travels, and CategoryDTO simply has no books field.
    @OneToMany(mappedBy = "category")
    private List<Book> books = new ArrayList<>();

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
