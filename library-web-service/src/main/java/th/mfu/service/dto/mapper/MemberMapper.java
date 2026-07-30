package th.mfu.service.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.domain.Member;
import th.mfu.service.dto.MemberDTO;

/**
 * The Assembler for Member.
 */
@Mapper(componentModel = "spring")
public interface MemberMapper {

    void updateMemberFromEntity(Member entity, @MappingTarget MemberDTO dto);

    /**
     * DTO -> entity, for a create or a PARTIAL UPDATE.
     *
     * The rule is the same in all three mappers: <b>ignore the id and the
     * relationships, never a plain data field.</b>
     *
     * <ul>
     * <li>id - the controller decides it. It comes from the URL, or from the
     * database on a create.</li>
     * <li>transactions - a relationship. It is filled by borrowing a book, not
     * by sending a member.</li>
     * </ul>
     *
     * name, email and joinDate are ordinary fields and have to be copied.
     * Putting name in the ignore list means POST /members always stores a member
     * with no name, and PATCH can never correct one.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    void updateMemberFromDto(MemberDTO dto, @MappingTarget Member entity);
}
