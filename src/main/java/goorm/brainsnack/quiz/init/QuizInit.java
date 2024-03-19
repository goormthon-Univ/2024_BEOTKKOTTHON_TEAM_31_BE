package goorm.brainsnack.quiz.init;

import goorm.brainsnack.quiz.domain.Quiz;
import goorm.brainsnack.quiz.domain.QuizCategory;
import goorm.brainsnack.quiz.repository.QuizRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class QuizInit {

    private final InitService initService;

    @PostConstruct
    public void init() throws FileNotFoundException {
        initService.init();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final QuizRepository quizRepository;

        public void init() throws FileNotFoundException {
            try{
                // 엑셀 파일 경로 공통화 시키기 위해서 변경할 필요가 있음
                String excelFilePath = "/Users/supportkim/Desktop/test.xlsx";
                FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));

                // 워크북(엑셀 파일) 생성
                Workbook workbook = WorkbookFactory.create(fileInputStream);

                // 첫 번째 시트 선택
                Sheet sheet = workbook.getSheetAt(0);

                // 헤더 행은 건너뛰기 위해 반복자를 첫 번째 데이터 행으로 이동
                Iterator<Row> rowIterator = sheet.iterator();

                if (rowIterator.hasNext()) {
                    rowIterator.next(); // 헤더 행은 건너뜁니다.
                }


                // 예외가 있는 문제 (보기가 숫자로 인식)
                int[] exceptionRowNumbers = {19, 27, 28, 29, 30};

                // 각 행을 순회하며 데이터를 읽어서 객체로 변환하여 리스트에 추가
                while (rowIterator.hasNext()) {

                    // 보기가 숫자로 인식되는 문제들은 createExceptionQuiz 로 만들어집니다.
                    Row row = rowIterator.next();
                    if (Arrays.stream(exceptionRowNumbers).anyMatch(rowNum -> rowNum == row.getRowNum())) {
                        Quiz quiz = createExceptionQuiz(row);
                        quizRepository.save(quiz);
                        continue;
                    }

                    Quiz quiz = createQuiz(row);
                    quizRepository.save(quiz);
                }
                // 리소스 정리
                workbook.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static Quiz createQuiz(Row row) {
            String category = row.getCell(0).getStringCellValue();
            QuizCategory quizCategory = QuizCategory.LANG;
            if (category.equals("math")) {
                quizCategory = QuizCategory.MATH;
            } else if (category.equals("deduce")) {
                quizCategory = QuizCategory.DEDUCE;
            }
            int quizNum = (int) row.getCell(1).getNumericCellValue();
            String title = row.getCell(2).getStringCellValue();
            String example = row.getCell(3).getStringCellValue();
            String choiceFirst = row.getCell(4).getStringCellValue();
            String choiceSecond = row.getCell(5).getStringCellValue();
            String choiceThird = row.getCell(6).getStringCellValue();
            String choiceFourth = row.getCell(7).getStringCellValue();
            String choiceFifth = row.getCell(8).getStringCellValue();
            int answer = (int) row.getCell(9).getNumericCellValue();
            String solution = row.getCell(10).getStringCellValue();
            Quiz quiz = Quiz.builder()
                    .category(quizCategory)
                    .quizNum(quizNum)
                    .title(title)
                    .example(example)
                    .choiceFirst(choiceFirst)
                    .choiceSecond(choiceSecond)
                    .choiceThird(choiceThird)
                    .choiceFourth(choiceFourth)
                    .choiceFifth(choiceFifth)
                    .answer(answer)
                    .solution(solution)
                    .isSimilar(false)
                    .build();
            return quiz;
        }
        private static Quiz createExceptionQuiz(Row row) {
            String category = row.getCell(0).getStringCellValue();
            QuizCategory quizCategory = QuizCategory.LANG;
            if (category.equals("math")) {
                quizCategory = QuizCategory.MATH;
            } else if (category.equals("deduce")) {
                quizCategory = QuizCategory.DEDUCE;
            }
            int quizNum = (int) row.getCell(1).getNumericCellValue();
            String title = row.getCell(2).getStringCellValue();
            String example = row.getCell(3).getStringCellValue();
            String choiceFirst = String.valueOf(row.getCell(4).getNumericCellValue());
            String choiceSecond = String.valueOf(row.getCell(5).getNumericCellValue());
            String choiceThird = String.valueOf(row.getCell(6).getNumericCellValue());
            String choiceFourth = String.valueOf(row.getCell(7).getNumericCellValue());
            String choiceFifth = String.valueOf(row.getCell(8).getNumericCellValue());
            int answer = (int) row.getCell(9).getNumericCellValue();
            String solution = row.getCell(10).getStringCellValue();
            Quiz quiz = Quiz.builder()
                    .category(quizCategory)
                    .quizNum(quizNum)
                    .title(title)
                    .example(example)
                    .choiceFirst(choiceFirst)
                    .choiceSecond(choiceSecond)
                    .choiceThird(choiceThird)
                    .choiceFourth(choiceFourth)
                    .choiceFifth(choiceFifth)
                    .answer(answer)
                    .solution(solution)
                    .isSimilar(false)
                    .build();
            return quiz;
        }
    }
}