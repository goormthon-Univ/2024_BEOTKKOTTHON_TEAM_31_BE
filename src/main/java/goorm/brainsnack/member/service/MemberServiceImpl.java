package goorm.brainsnack.member.service;

import goorm.brainsnack.exception.BaseException;
import goorm.brainsnack.exception.ErrorCode;
import goorm.brainsnack.member.domain.Member;
import goorm.brainsnack.member.dto.MemberResponseDto;
import goorm.brainsnack.member.repository.MemberRepository;
import goorm.brainsnack.quiz.domain.MemberQuiz;
import goorm.brainsnack.quiz.domain.Quiz;
import goorm.brainsnack.quiz.domain.QuizCategory;
import goorm.brainsnack.quiz.dto.MemberQuizResponseDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto;
import goorm.brainsnack.quiz.dto.SimilarQuizResponseDto;
import goorm.brainsnack.quiz.repository.MemberQuizRepository;
import goorm.brainsnack.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static goorm.brainsnack.member.dto.MemberResponseDto.*;
import static goorm.brainsnack.quiz.domain.MemberQuiz.*;
import static goorm.brainsnack.quiz.dto.MemberQuizResponseDto.*;
import static goorm.brainsnack.quiz.dto.SimilarQuizResponseDto.*;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    @Transactional
    @Override
    public LoginDto login(String temporaryId) {
        Optional<Member> optionalMember = memberRepository.findByTemporaryId(temporaryId);

        //기존 멤버 확인
        if (optionalMember.isPresent()) {
            return Member.toMemberRequestDto(optionalMember.get());
        }

        Member member = Member.from(temporaryId);
        memberRepository.save(member);

        return Member.toMemberRequestDto(member);
    }

}
