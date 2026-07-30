package th.mfu.service.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.domain.Book;
import th.mfu.service.dto.BookDTO;

/**
 * The Assembler for Book: it copies data between the entity and the DTO so the
 * two classes never need to know about each other.
 *
 * You write the interface. MapStruct writes the class - look in
 * target/generated-sources/annotations/ after a build and you will find
 * BookMapperImpl.java, full of plain getters and setters. No reflection, no
 * magic at runtime; it is generated at COMPILE time, which is why the
 * mapstruct-processor is listed in the parent pom.
 *
 * componentModel = "spring" makes the generated class a @Component, so it can
 * be @Autowired into a controller.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    /**
     * Entity -> DTO, for sending a book out.
     *
     * The two @Mapping lines flatten the relationship: the entity's
     * category.id and category.name become two plain fields on the DTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    void updateBookFromEntity(Book entity, @MappingTarget BookDTO dto);

    /**
     * DTO -> entity, for a create or a PARTIAL UPDATE.
     *
     * This is the important one. nullValuePropertyMappingStrategy = IGNORE
     * means: <b>if a field of the DTO is null, do not touch the entity.</b>
     *
     * So a PATCH body of { "title": "New title" } changes the title and leaves
     * the author, the year and the date exactly as they were. Without IGNORE,
     * the three missing fields would be copied over as null and the update
     * would quietly wipe them.
     *
     * id and category are ignored because the controller decides those: the id
     * comes from the URL, and the category has to be looked up as a real row.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    void updateBookFromDto(BookDTO dto, @MappingTarget Book entity);
}
