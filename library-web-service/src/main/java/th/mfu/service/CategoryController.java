package th.mfu.service;

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
import th.mfu.domain.Category;
import th.mfu.service.dto.BookDTO;
import th.mfu.service.dto.CategoryDTO;
import th.mfu.service.dto.mapper.BookMapper;
import th.mfu.service.dto.mapper.CategoryMapper;
import th.mfu.service.repository.BookRepository;
import th.mfu.service.repository.CategoryRepository;

/**
 * Categories, and the books inside them.
 */
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BookMapper bookMapper;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> dtos = new ArrayList<CategoryDTO>();
        for (Category category : categoryRepository.findAll()) {
            CategoryDTO dto = new CategoryDTO();
            categoryMapper.updateCategoryFromEntity(category, dto);
            dtos.add(dto);
        }
        return new ResponseEntity<List<CategoryDTO>>(dtos, HttpStatus.OK);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            return new ResponseEntity<CategoryDTO>(HttpStatus.NOT_FOUND);
        }
        CategoryDTO dto = new CategoryDTO();
        categoryMapper.updateCategoryFromEntity(category.get(), dto);
        return new ResponseEntity<CategoryDTO>(dto, HttpStatus.OK);
    }

    /**
     * GET /categories/{id}/books -> 200 with the books, or 404.
     *
     * The entity Category HAS a list of books, but CategoryDTO deliberately
     * does not. A client that wants the books asks for them here, and gets a
     * list of BookDTO - no nesting, no loop, no surprise 10 MB response.
     */
    @GetMapping("/categories/{id}/books")
    public ResponseEntity<List<BookDTO>> getBooksInCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return new ResponseEntity<List<BookDTO>>(HttpStatus.NOT_FOUND);
        }
        List<BookDTO> dtos = new ArrayList<BookDTO>();
        for (Book book : bookRepository.findByCategoryId(id)) {
            BookDTO dto = new BookDTO();
            bookMapper.updateBookFromEntity(book, dto);
            dtos.add(dto);
        }
        return new ResponseEntity<List<BookDTO>>(dtos, HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        Category category = new Category();
        categoryMapper.updateCategoryFromDto(dto, category);
        Category saved = categoryRepository.save(category);

        CategoryDTO result = new CategoryDTO();
        categoryMapper.updateCategoryFromEntity(saved, result);
        return new ResponseEntity<CategoryDTO>(result, HttpStatus.CREATED);
    }
}
