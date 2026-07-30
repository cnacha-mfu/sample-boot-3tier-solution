package th.mfu.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.mfu.domain.Book;
import th.mfu.domain.Member;
import th.mfu.domain.Transaction;
import th.mfu.service.dto.TransactionDTO;
import th.mfu.service.dto.mapper.TransactionMapper;
import th.mfu.service.repository.BookRepository;
import th.mfu.service.repository.MemberRepository;
import th.mfu.service.repository.TransactionRepository;

/**
 * Borrowing and returning books.
 *
 * The request body is back to the shape library-design.md asked for:
 *
 *     { "book_id": 3, "member_id": 2, "type": "borrow" }
 *
 * In the JPA sample the entity was used as the request body, so the client had
 * to send a nested { "book": { "id": 3 } } instead. Nothing about the database
 * changed - only that a DTO now stands between the two, and the wire format is
 * free to be whatever suits the client.
 */
@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> dtos = new ArrayList<TransactionDTO>();
        for (Transaction transaction : transactionRepository.findAll()) {
            dtos.add(toDto(transaction));
        }
        return new ResponseEntity<List<TransactionDTO>>(dtos, HttpStatus.OK);
    }

    /** The borrowing history of one member. */
    @GetMapping("/members/{id}/transactions")
    public ResponseEntity<List<TransactionDTO>> getMemberTransactions(@PathVariable Long id) {
        if (!memberRepository.existsById(id)) {
            return new ResponseEntity<List<TransactionDTO>>(HttpStatus.NOT_FOUND);
        }
        List<TransactionDTO> dtos = new ArrayList<TransactionDTO>();
        for (Transaction transaction : transactionRepository.findByMemberId(id)) {
            dtos.add(toDto(transaction));
        }
        return new ResponseEntity<List<TransactionDTO>>(dtos, HttpStatus.OK);
    }

    /** POST /transactions -> 201, body { book_id, member_id, type }. */
    @PostMapping("/transactions")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionDTO dto) {
        return record(dto.getBookId(), dto.getMemberId(), dto.getType());
    }

    /** POST /members/{id}/transactions -> 201, body { book_id, type }. */
    @PostMapping("/members/{id}/transactions")
    public ResponseEntity<?> createMemberTransaction(@PathVariable Long id, @RequestBody TransactionDTO dto) {
        return record(dto.getBookId(), id, dto.getType());
    }

    /** The borrow / return rules, shared by both POST endpoints. */
    private ResponseEntity<?> record(Long bookId, Long memberId, String type) {
        if (bookId == null || memberId == null) {
            return new ResponseEntity<String>("book_id and member_id are required", HttpStatus.BAD_REQUEST);
        }
        if (!Transaction.BORROW.equals(type) && !Transaction.RETURN.equals(type)) {
            return new ResponseEntity<String>("type must be 'borrow' or 'return'", HttpStatus.BAD_REQUEST);
        }

        Optional<Book> book = bookRepository.findById(bookId);
        if (!book.isPresent()) {
            return new ResponseEntity<String>("No book with ID: " + bookId, HttpStatus.NOT_FOUND);
        }
        Optional<Member> member = memberRepository.findById(memberId);
        if (!member.isPresent()) {
            return new ResponseEntity<String>("No member with ID: " + memberId, HttpStatus.NOT_FOUND);
        }

        // The newest transaction for this book tells us whether it is on loan.
        Transaction latest = transactionRepository.findFirstByBookIdOrderByIdDesc(bookId);
        boolean onLoan = latest != null && Transaction.BORROW.equals(latest.getType());

        if (Transaction.BORROW.equals(type) && onLoan) {
            return new ResponseEntity<String>("Book " + bookId + " is already borrowed", HttpStatus.BAD_REQUEST);
        }
        if (Transaction.RETURN.equals(type)) {
            if (!onLoan) {
                return new ResponseEntity<String>("Book " + bookId + " is not currently borrowed",
                        HttpStatus.BAD_REQUEST);
            }
            if (!latest.getMember().getId().equals(memberId)) {
                return new ResponseEntity<String>("Book " + bookId + " was borrowed by member "
                        + latest.getMember().getId() + ", not by member " + memberId, HttpStatus.BAD_REQUEST);
            }
        }

        Transaction saved = transactionRepository.save(
                new Transaction(type, LocalDate.now(), book.get(), member.get()));
        return new ResponseEntity<TransactionDTO>(toDto(saved), HttpStatus.CREATED);
    }

    private TransactionDTO toDto(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        transactionMapper.updateTransactionFromEntity(transaction, dto);
        return dto;
    }
}
