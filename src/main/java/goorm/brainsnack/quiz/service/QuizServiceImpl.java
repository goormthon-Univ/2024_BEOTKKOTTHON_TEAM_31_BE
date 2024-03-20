package goorm.brainsnack.quiz.service;


import goorm.brainsnack.exception.BaseException;
import goorm.brainsnack.exception.ErrorCode;
import goorm.brainsnack.exception.QuizException;
import goorm.brainsnack.member.domain.Member;
import goorm.brainsnack.member.repository.MemberRepository;
import goorm.brainsnack.quiz.domain.MemberQuiz;
import goorm.brainsnack.quiz.domain.Quiz;
import goorm.brainsnack.quiz.domain.QuizCategory;
import goorm.brainsnack.quiz.domain.QuizData;
import goorm.brainsnack.quiz.repository.MemberQuizRepository;
import goorm.brainsnack.quiz.repository.QuizDataRepository;
import goorm.brainsnack.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static goorm.brainsnack.quiz.dto.QuizRequestDto.*;
import static goorm.brainsnack.quiz.dto.QuizResponseDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final MemberRepository memberRepository;
    private final MemberQuizRepository memberQuizRepository;
    private final QuizRepository quizRepository;
    private final QuizDataRepository dataRepository;

    @Override
    public QuizDetailDto findQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizException(ErrorCode.NOT_EXIST_QUIZ));
        // 여기서 DTO 로 반환해서 Controller 에게 넘겨주기
        return QuizDetailDto.from(quiz);
    }
    
    @Override
    public GetTotalMemberDto getTotalNum(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        List<MemberQuiz> members = memberQuizRepository.findAllByMember(member);

        // 풀었던 퀴즈가 없는 경우
        if (members.isEmpty()) {
            return GetTotalMemberDto.from(0);
        }

        int totalQuizNum = memberQuizRepository.findAllByMember(member).size();
        return GetTotalMemberDto.from(totalQuizNum);
    }

    @Override
    public CategoryQuizListDto getCategoryQuizList(String categoryName) {
        QuizCategory category = QuizCategory.getInstance(categoryName);

        List<Quiz> quizList = quizRepository.findAllByCategoryAndIsSimilar(category, false);

        return CategoryQuizListDto.builder()
                .size(quizList.size())
                .quizList(quizList.stream()
                        .map(SingleQuizDto::from)
                        .toList())
                .build();
    }

    @Transactional
    @Override
    public FullGradeDto gradeFullQuiz(Long memberId, String category, FullGradeRequestDto request) {
        return FullGradeDto.from(request.getGradeRequestList().stream()
                .map(r -> gradeSingleQuiz(memberId, r.getQuizId(), r))
                .toList());
    }

    @Transactional
    @Override
    public SingleGradeDto gradeSingleQuiz(Long memberId, Long quizId, SingleGradeRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_QUIZ));

        if (memberQuizRepository.findByMemberAndQuiz(member, quiz).isPresent()) {
            throw new BaseException(ErrorCode.ALREADY_FINISH_QUIZ);
        }

        MemberQuiz memberQuiz = memberQuizRepository.save(MemberQuiz.of(request, member, quiz));
        Optional<QuizData> optionalQuizData = dataRepository.findByQuiz(quiz);
        QuizData data = optionalQuizData.orElseGet(() -> dataRepository.save(QuizData.from(quiz)));
        data.updateQuizData(memberQuiz);

        int ratio = 0;
        if (data.getQuizParticipantsNum() != 0) {
            ratio = data.getCorrectAnswerNum() / data.getQuizParticipantsNum();
        }

        return SingleGradeDto.of(quiz, memberQuiz, data, ratio);
    }


    @Override
    public FullResultResponseDto getFullResult(Long memberId, String categoryInput, FullResultRequestDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_USER));
        List<MemberQuiz> memberQuizList = memberQuizRepository.findAllByMemberQuizAndCategory(member, QuizCategory.getInstance(categoryInput));
        QuizCategory category = QuizCategory.getInstance(categoryInput);

        int totalQuizNum = quizRepository.findAllByCategoryAndIsSimilar(category, false).size();
        int wrongQuizNum = memberQuizRepository.findAllByMemberAndCategoryAndIsCorrect(member, false, category).size();

        return FullResultResponseDto.of(totalQuizNum, wrongQuizNum, memberQuizList, category);
    }

}
