package th.mfu.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A book in the library.
 *
 * Compare this class with the one from the JPA sample. Every Jackson annotation
 * is gone; only the JPA ones are left. That is the whole point of the DTO
 * pattern: the entity belongs to the data source layer, so it should not also
 * carry the rules for how it looks on the wire.
 *
 * @see th.mfu.service.dto.BookDTO for where those rules went.
 */
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    // Integer, not int. A primitive cannot be null, and the partial update in
    // BookMapper works by skipping the fields that ARE null.
    private Integer year;

    private LocalDate addedDate;

    // Many books belong to one category -> this class holds the column category_id.
    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "book")
    private List<Transaction> transactions = new ArrayList<>();

    /** JPA requires a no-argument constructor to build objects from rows. */
    public Book() {
    }

    public Book(String title, String author, Integer year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

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

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
