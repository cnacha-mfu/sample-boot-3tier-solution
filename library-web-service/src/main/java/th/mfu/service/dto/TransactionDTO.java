package th.mfu.service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * What a transaction looks like on the wire.
 *
 * The entity holds a Book object and a Member object. This holds their ids -
 * plus their titles and names, so a client can show a readable list without
 * fetching each book and each member one by one.
 *
 * This is also where the request shape from library-design.md comes back:
 * { "book_id": 3, "member_id": 2, "type": "borrow" }. In the JPA sample the
 * entity was used as the request body, so the client had to send a nested
 * { "book": { "id": 3 } }. With a DTO the wire format is free again.
 */
public class TransactionDTO {

    private Long id;

    /** Either "borrow" or "return". */
    private String type;

    @JsonProperty("transaction-date")
    private LocalDate transactionDate;

    @JsonProperty("book_id")
    private Long bookId;

    @JsonProperty("book_title")
    private String bookTitle;

    @JsonProperty("member_id")
    private Long memberId;

    @JsonProperty("member_name")
    private String memberName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
