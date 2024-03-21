package goorm.brainsnack.quiz.repository;


import goorm.brainsnack.quiz.domain.Quiz;
import goorm.brainsnack.quiz.domain.QuizCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByCategory(QuizCategory category);

    Optional<Quiz> findQuizByCategoryAndQuizNum(QuizCategory category , int quizNum);

}
