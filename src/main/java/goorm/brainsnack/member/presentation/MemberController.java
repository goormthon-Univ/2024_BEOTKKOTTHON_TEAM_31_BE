package goorm.brainsnack.member.presentation;
import goorm.brainsnack.global.BaseResponse;
import goorm.brainsnack.member.service.MemberService;
import goorm.brainsnack.quiz.dto.MemberQuizResponseDto;
import goorm.brainsnack.quiz.dto.SimilarQuizResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static goorm.brainsnack.member.dto.MemberResponseDto.LoginDto;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login/{temporaryId}")
    public ResponseEntity<BaseResponse<LoginDto>> login(@PathVariable String temporaryId) {
        return ResponseEntity.ok().body(new BaseResponse<>(memberService.login(temporaryId)));
    }

    // 내가 틀린 문제 조회 (기존 문제)
    @GetMapping("/members/{memberId}/quiz/wrong/{category}")
    public ResponseEntity<BaseResponse<List<MemberQuizResponseDto.MemberQuizDto>>> getWrongQuizList(@PathVariable Long memberId,
                                                                                                    @PathVariable String category) {
        List<MemberQuizResponseDto.MemberQuizDto> quizDtoList = memberService.getWrongQuizList(memberId,category);
        return ResponseEntity.ok().body(new BaseResponse<>(quizDtoList));
    }

    // 내가 맞은 문제 조회 (기존 문제)
    @GetMapping("/members/{memberId}/quiz/correct/{category}")
    public ResponseEntity<BaseResponse<List<MemberQuizResponseDto.MemberQuizDto>>> getCorrectQuizList(@PathVariable Long memberId,
                                                                                                      @PathVariable String category) {
        List<MemberQuizResponseDto.MemberQuizDto> quizDtoList = memberService.getCorrectQuizList(memberId,category);
        return ResponseEntity.ok().body(new BaseResponse<>(quizDtoList));
    }

    // 내가 생성한 유사 문제 조회
    @GetMapping("/members/{memberId}/similar-quiz/{quizId}/{category}")
    public ResponseEntity<BaseResponse<SimilarQuizResponseDto.MemberSimilarQuizDto>> getSimilarQuizList(@PathVariable Long memberId,
                                                                                                        @PathVariable Long quizId,
                                                                                                        @PathVariable String category) {
        // Category 가 필요하지 않을수도 -> quizId 로도 category 를 알 수 있기 때문?
        SimilarQuizResponseDto.MemberSimilarQuizDto result = memberService.getSimilarQuiz(memberId, quizId, category);
        return ResponseEntity.ok().body(new BaseResponse<>(result));
    }
}
