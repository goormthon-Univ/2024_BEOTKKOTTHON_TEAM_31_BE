package goorm.brainsnack.quiz.service;

import goorm.brainsnack.quiz.dto.QuizResponseDto.GetTotalMemberDto;

import static goorm.brainsnack.quiz.dto.QuizRequestDto.*;
import static goorm.brainsnack.quiz.dto.QuizResponseDto.*;

public interface QuizService {
  
    GetTotalMemberDto getTotalNum(Long memberId);
    CategoryQuizListDto getCategoryQuizList(String category);

    SingleGradeDto gradeSingleQuiz(Long memberId, Long quizId, SingleGradeRequestDto request);

    FullGradeDto gradeFullQuiz(Long memberId, String category, FullGradeRequestDto request);
}
