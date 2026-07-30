package th.mfu.service.repository;

import org.springframework.data.repository.CrudRepository;

import th.mfu.domain.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
