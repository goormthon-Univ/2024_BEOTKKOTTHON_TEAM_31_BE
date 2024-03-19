package goorm.brainsnack.quiz.service;

import goorm.brainsnack.exception.BaseException;
import goorm.brainsnack.exception.ErrorCode;
import goorm.brainsnack.member.domain.Member;
import goorm.brainsnack.member.repository.MemberRepository;
import goorm.brainsnack.quiz.domain.MemberQuiz;
import goorm.brainsnack.quiz.domain.Quiz;
import goorm.brainsnack.quiz.domain.QuizCategory;
import goorm.brainsnack.quiz.domain.QuizData;
import goorm.brainsnack.quiz.dto.QuizResponseDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto.CategoryQuizListDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto.SingleGradeQuizDto;
import goorm.brainsnack.quiz.repository.MemberQuizRepository;
import goorm.brainsnack.quiz.repository.QuizDataRepository;
import goorm.brainsnack.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static goorm.brainsnack.quiz.dto.QuizRequestDto.SingleGradeRequestDto;
import static goorm.brainsnack.quiz.dto.QuizResponseDto.QuizDetailDto;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final MemberRepository memberRepository;
    private final MemberQuizRepository memberQuizRepository;
    private final QuizRepository quizRepository;
    private final QuizDataRepository dataRepository;
    
    @Override
    public QuizResponseDto.GetTotalMemberDto getTotalNum(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        List<MemberQuiz> members = memberQuizRepository.findAllByMember(member);

        // 풀었던 퀴즈가 없는 경우
        if (members.isEmpty()) {
            return QuizResponseDto.GetTotalMemberDto.from(0);
        }

        int totalQuizNum = memberQuizRepository.findAllByMember(member).size();
        return QuizResponseDto.GetTotalMemberDto.from(totalQuizNum);
    }

    @Override
    public CategoryQuizListDto getCategoryQuizList(String categoryName) {
        QuizCategory category = QuizCategory.getInstance(categoryName);

        List<Quiz> quizList = quizRepository.findAllByCategory(category);

        return CategoryQuizListDto.builder()
                .size(quizList.size())
                .quizDetailDtoList(quizList.stream()
                        .map(QuizDetailDto::from)
                        .toList())
                .build();
    }

    @Transactional
    @Override
    public SingleGradeQuizDto gradeSingleQuiz(Long memberId, Long quizId, SingleGradeRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_QUIZ));

        MemberQuiz memberQuiz = memberQuizRepository.save(MemberQuiz.of(request, member, quiz));

        QuizData data = dataRepository.findByQuiz(quiz)
                .orElse(QuizData.from(quiz));
        data.updateQuizData(memberQuiz);

        int ratio = 0;
        if (data.getQuizParticipantsNum() != 0) {
            ratio = data.getCorrectAnswerNum() / data.getQuizParticipantsNum();
        }

        return SingleGradeQuizDto.of(quiz, memberQuiz, data, ratio);
    }

}
