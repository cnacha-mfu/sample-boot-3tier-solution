package th.mfu.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.mfu.domain.Book;

/**
 * Spring Data repository for Book.
 *
 * Unchanged from the JPA sample except for the import: Book now comes from the
 * other module. The repository interface did not have to care that the class
 * moved.
 */
public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByCategoryId(Long categoryId);

    Book findByTitle(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);
}
