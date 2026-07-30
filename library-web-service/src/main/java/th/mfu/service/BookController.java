package th.mfu.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.mfu.domain.Book;
import th.mfu.domain.Category;
import th.mfu.service.dto.BookDTO;
import th.mfu.service.dto.mapper.BookMapper;
import th.mfu.service.repository.BookRepository;
import th.mfu.service.repository.CategoryRepository;

/**
 * Books.
 *
 * Every method follows the same three steps, and that shape is the lesson:
 *
 *     1. talk to the repository, in ENTITIES
 *     2. use the mapper to convert
 *     3. answer the client, in DTOs
 *
 * An entity never leaves this class, and a DTO never reaches the database.
 */
@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookMapper bookMapper;

    /** GET /books -> 200 with the list. */
    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> getAllBooks(@RequestParam(required = false) String author) {
        Iterable<Book> books;
        if (author != null) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else {
            books = bookRepository.findAll();
        }

        List<BookDTO> dtos = new ArrayList<BookDTO>();
        for (Book book : books) {
            dtos.add(toDto(book));
        }
        return new ResponseEntity<List<BookDTO>>(dtos, HttpStatus.OK);
    }

    /** GET /books/{id} -> 200 with the book, or 404. */
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()) {
            return new ResponseEntity<BookDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BookDTO>(toDto(book.get()), HttpStatus.OK);
    }

    /** POST /books -> 201 with the created book. The database gives the id. */
    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO dto) {
        Book book = new Book();
        bookMapper.updateBookFromDto(dto, book);

        if (!applyCategory(dto, book)) {
            return new ResponseEntity<BookDTO>(HttpStatus.BAD_REQUEST);
        }
        if (book.getAddedDate() == null) {
            book.setAddedDate(LocalDate.now());
        }

        Book saved = bookRepository.save(book);
        return new ResponseEntity<BookDTO>(toDto(saved), HttpStatus.CREATED);
    }

    /**
     * PUT /books/{id} -> 204, or 404.
     *
     * PUT REPLACES. Whatever the body leaves out is set to null, because a new
     * Book object is built from scratch. Compare it with the PATCH below - that
     * difference is the whole point of having both.
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody BookDTO dto) {
        if (!bookRepository.existsById(id)) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        Book book = new Book();
        bookMapper.updateBookFromDto(dto, book);
        if (!applyCategory(dto, book)) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        book.setId(id);
        bookRepository.save(book);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /**
     * PATCH /books/{id} -> 200 with the updated book, or 404.
     *
     * PARTIAL UPDATE - the point of the MapStruct lesson.
     *
     * The existing row is loaded first, and the mapper copies over only the
     * fields the client actually sent. Send { "title": "New title" } and the
     * author, year and date survive untouched. That works because
     * BookMapper.updateBookFromDto is annotated
     * nullValuePropertyMappingStrategy = IGNORE.
     */
    @PatchMapping("/books/{id}")
    public ResponseEntity<BookDTO> patchBook(@PathVariable Long id, @RequestBody BookDTO dto) {
        Optional<Book> existing = bookRepository.findById(id);
        if (!existing.isPresent()) {
            return new ResponseEntity<BookDTO>(HttpStatus.NOT_FOUND);
        }

        Book book = existing.get();
        // Load, then merge on top. Nothing is lost.
        bookMapper.updateBookFromDto(dto, book);
        if (!applyCategory(dto, book)) {
            return new ResponseEntity<BookDTO>(HttpStatus.BAD_REQUEST);
        }

        Book saved = bookRepository.save(book);
        return new ResponseEntity<BookDTO>(toDto(saved), HttpStatus.OK);
    }

    /** DELETE /books/{id} -> 204, or 404. */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        bookRepository.deleteById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /** DELETE /books -> 204. Used by the integration test to start clean. */
    @DeleteMapping("/books")
    public ResponseEntity<Void> deleteAllBooks() {
        bookRepository.deleteAll();
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /**
     * Entity -> DTO. The mapper fills a fresh DTO, which is why every method
     * ends with one of these instead of returning the entity.
     */
    private BookDTO toDto(Book book) {
        BookDTO dto = new BookDTO();
        bookMapper.updateBookFromEntity(book, dto);
        return dto;
    }

    /**
     * The DTO carries category_id, a plain number. The entity needs a real
     * Category row. Turning one into the other is a job the mapper cannot do,
     * because it needs the database - so the controller does it.
     *
     * @return false when the client named a category that does not exist.
     */
    private boolean applyCategory(BookDTO dto, Book book) {
        if (dto.getCategoryId() == null) {
            return true;
        }
        Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
        if (!category.isPresent()) {
            return false;
        }
        book.setCategory(category.get());
        return true;
    }
}
