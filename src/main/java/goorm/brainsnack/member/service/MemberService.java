package goorm.brainsnack.member.service;

import goorm.brainsnack.member.dto.MemberResponseDto;
import goorm.brainsnack.quiz.dto.MemberQuizResponseDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto;
import goorm.brainsnack.quiz.dto.SimilarQuizResponseDto;

import java.util.List;

public interface MemberService {
    MemberResponseDto.LoginDto login(String temporaryId);

    MemberResponseDto.MemberDto findById(Long memberId);

    List<MemberQuizResponseDto.MemberQuizDto> getWrongQuizList(Long memberId , String category);
    List<MemberQuizResponseDto.MemberQuizDto> getCorrectQuizList(Long memberId , String category);

    SimilarQuizResponseDto.MemberSimilarQuizDto getSimilarQuiz(Long memberId, Long quizId , String category);
}
