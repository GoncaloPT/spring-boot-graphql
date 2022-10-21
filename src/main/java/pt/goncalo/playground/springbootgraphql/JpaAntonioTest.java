package pt.goncalo.playground.springbootgraphql;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pt.goncalo.playground.springbootgraphql.repository.QuizRepository;
import pt.goncalo.playground.springbootgraphql.repository.QuizTypeRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.Company;
import pt.goncalo.playground.springbootgraphql.repository.entity.Quiz;
import pt.goncalo.playground.springbootgraphql.repository.entity.QuizType;

import java.util.UUID;

@Component
public class JpaAntonioTest implements ApplicationRunner {
    private final QuizRepository quizRepository;

    public JpaAntonioTest(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var quizType = new QuizType(null, "myType");
        var company = new Company(null,"MY NEW COMPANY");
        var quiz = new Quiz(null,"title", "description",quizType, company);

        var createdQuiz = quizRepository.save(quiz);
        System.out.println("created quiz is: " + createdQuiz);
    }
}
