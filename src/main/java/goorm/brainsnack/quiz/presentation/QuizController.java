package goorm.brainsnack.quiz.presentation;

import goorm.brainsnack.global.BaseResponse;
import goorm.brainsnack.quiz.dto.QuizRequestDto.SingleGradeRequestDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto.CategoryQuizListDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto.GetTotalMemberDto;
import goorm.brainsnack.quiz.dto.QuizResponseDto.SingleGradeQuizDto;
import goorm.brainsnack.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/quiz/{category}")
    public ResponseEntity<BaseResponse<CategoryQuizListDto>> getCategoryQuizList(@PathVariable String category) {
        return ResponseEntity.ok().body(new BaseResponse<>(quizService.getCategoryQuizList(category)));
    }

    @GetMapping("/members/{member-id}")
    public ResponseEntity<BaseResponse<GetTotalMemberDto>> getTotalMember(@PathVariable("member-id") Long memberId) {
        return ResponseEntity.ok().body(new BaseResponse<>(quizService.getTotalNum(memberId)));
    }

    @PostMapping("/members/{member-id}/quiz/{quiz-id}/grade")
    public ResponseEntity<BaseResponse<SingleGradeQuizDto>> gradeSingleQuiz(@PathVariable("member-id") Long memberId,
                                                                            @PathVariable("quiz-id") Long quizId,
                                                                            @RequestBody SingleGradeRequestDto request) {
        return ResponseEntity.ok().body(new BaseResponse<>(quizService.gradeSingleQuiz(memberId, quizId, request)));
    }


}
