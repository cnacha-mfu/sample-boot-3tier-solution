package th.mfu.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.mfu.domain.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findByMemberId(Long memberId);

    /** Newest transaction for a book - used to work out if it is on loan. */
    Transaction findFirstByBookIdOrderByIdDesc(Long bookId);
}
