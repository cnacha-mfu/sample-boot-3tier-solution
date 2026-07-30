package th.mfu.service.repository;

import org.springframework.data.repository.CrudRepository;

import th.mfu.domain.Member;

public interface MemberRepository extends CrudRepository<Member, Long> {
}
