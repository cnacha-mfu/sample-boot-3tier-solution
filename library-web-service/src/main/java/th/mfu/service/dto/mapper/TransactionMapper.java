package th.mfu.service.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.domain.Transaction;
import th.mfu.service.dto.TransactionDTO;

/**
 * The Assembler for Transaction.
 *
 * This one flattens TWO relationships at once: the entity points at a Book and
 * a Member, and the DTO turns each of them into an id plus a readable label.
 * A client can render "Malee Rakdee borrowed Spring in Action" from a single
 * response, with no extra requests.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "member.name", target = "memberName")
    void updateTransactionFromEntity(Transaction entity, @MappingTarget TransactionDTO dto);
}
