package th.mfu.service.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.domain.Category;
import th.mfu.service.dto.CategoryDTO;

/**
 * The Assembler for Category.
 *
 * Nothing needs flattening here, so the two methods are as short as a mapper
 * ever gets: MapStruct matches id to id, name to name, description to
 * description, purely by name.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromEntity(Category entity, @MappingTarget CategoryDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateCategoryFromDto(CategoryDTO dto, @MappingTarget Category entity);
}
