package goorm.brainsnack.quiz.service;

import goorm.brainsnack.quiz.domain.QuizCategory;
import goorm.brainsnack.quiz.dto.ChatGPTRequestDto;
import goorm.brainsnack.quiz.dto.SimilarQuizResponseDto;

public interface ChatGPTService {
    SimilarQuizResponseDto.CreateDto prompt(ChatGPTRequestDto.ChatCompletionDto chatCompletionDto , QuizCategory quizCategory);
}
