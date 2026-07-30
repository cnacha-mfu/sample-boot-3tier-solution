package th.mfu.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A person who can borrow books from the library.
 *
 * MEMBER is a reserved word in MySQL 8, so the table is called library_member.
 * @Table is how you point an entity at a table with a different name.
 */
@Entity
@Table(name = "library_member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private LocalDate joinDate;

    @OneToMany(mappedBy = "member")
    private List<Transaction> transactions = new ArrayList<>();

    public Member() {
    }

    public Member(String name, String email, LocalDate joinDate) {
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
