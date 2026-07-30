package th.mfu.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.mfu.domain.Member;
import th.mfu.service.dto.MemberDTO;
import th.mfu.service.dto.mapper.MemberMapper;
import th.mfu.service.repository.MemberRepository;

/**
 * Members.
 */
@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    @GetMapping("/members")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        List<MemberDTO> dtos = new ArrayList<MemberDTO>();
        for (Member member : memberRepository.findAll()) {
            dtos.add(toDto(member));
        }
        return new ResponseEntity<List<MemberDTO>>(dtos, HttpStatus.OK);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (!member.isPresent()) {
            return new ResponseEntity<MemberDTO>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<MemberDTO>(toDto(member.get()), HttpStatus.OK);
    }

    @PostMapping("/members")
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO dto) {
        Member member = new Member();
        memberMapper.updateMemberFromDto(dto, member);
        if (member.getJoinDate() == null) {
            member.setJoinDate(LocalDate.now());
        }
        Member saved = memberRepository.save(member);
        return new ResponseEntity<MemberDTO>(toDto(saved), HttpStatus.CREATED);
    }

    /** Partial update again - change only the email, keep the rest. */
    @PatchMapping("/members/{id}")
    public ResponseEntity<MemberDTO> patchMember(@PathVariable Long id, @RequestBody MemberDTO dto) {
        Optional<Member> existing = memberRepository.findById(id);
        if (!existing.isPresent()) {
            return new ResponseEntity<MemberDTO>(HttpStatus.NOT_FOUND);
        }
        Member member = existing.get();
        memberMapper.updateMemberFromDto(dto, member);
        return new ResponseEntity<MemberDTO>(toDto(memberRepository.save(member)), HttpStatus.OK);
    }

    private MemberDTO toDto(Member member) {
        MemberDTO dto = new MemberDTO();
        memberMapper.updateMemberFromEntity(member, dto);
        return dto;
    }
}
