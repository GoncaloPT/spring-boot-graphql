package pt.goncalo.playground.springbootgraphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringBootGraphqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGraphqlApplication.class, args);
    }

}

@Controller
class QuizController {
    private static final Map<Integer, Quiz> db = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger("QuizController");
    private static AtomicInteger idGenerator = new AtomicInteger();

    static {
        db.put(idGenerator.incrementAndGet(), new Quiz(idGenerator.get(), "1 - title", "1 - Description"));
        db.put(idGenerator.incrementAndGet(), new Quiz(idGenerator.get(), "2 - title", "2 - Description"));
    }

    @QueryMapping
    Collection<Quiz> quiz() {
        return db.values();
    }

    @MutationMapping
    Quiz addQuiz(@Argument CreateQuiz quiz) {
        log.info("add quiz called with {}", quiz);
        var id = idGenerator.getAndIncrement();
        var toInsert = new Quiz(id, "2 - title", "2 - Description");
        db.put(id, toInsert);
        return toInsert;
    }

    @QueryMapping
    Quiz quizById(@Argument Integer id) {
        return db.get(id);
    }

    @BatchMapping
    Map<Quiz, List<Category>> categories(List<Quiz> quizzes) {
        log.info("BatchMapping categories called.");

        var idsToFetch = quizzes
                .stream()
                .map(Quiz::id).toList();
        log.info("BatchMapping categories called for ids #[{}]", idsToFetch);
        return quiz()
                .stream()
                .filter(quiz -> idsToFetch.contains(quiz.id()))
                .collect(
                        Collectors
                                .toMap(
                                        quiz -> quiz,
                                        quiz -> List.of(new Category(quiz.id(), "category of quiz " + quiz.title(), true))
                                ));
    }


    /**
     * This method fetches the categories of each quiz, one by one.
     * The performance of such operation can be very poor since the network
     * calls stack on each other:
     * Imagine a quiz with 100 categories, we would do 100 + 1 network calls to get the single
     * response the client is expecting.
     * <p>
     * To avoid this, we use @BatchRequest.
     * See: {@link #categories(List)}
     *
     * @param quiz
     * @return
     */
    //@SchemaMapping(typeName = "Quiz")
    List<Category> categories(Quiz quiz) {
        return List.of(new Category(quiz.id(), "category of quiz " + quiz.title(), true));
    }

    /**
     * This method fetches the questions of each category, one by one.
     * The performance of such operation can be very poor since the network
     * calls stack on each other:
     * Imagine a category with 100 questions, we would do 100 + 1 network calls to get the single
     * response the client is expecting.
     * <p>
     * To avoid this, we use @BatchRequest.
     * See: {@link #questions(List)}
     *
     * @param category
     * @return
     */
    @SchemaMapping(typeName = "Category")
    List<Question> questions(Category category) {
        return List
                .of(
                        new Question(
                                category.id(),
                                "do you " + category.name() + "?",
                                null,
                                false
                        )
                );
    }

}

@Controller
class CategoryController {

}

record Quiz(Integer id, String title, String description) {
}

record CreateQuiz(String title, String description, List<CreateCategory> categories) {
}

record Category(Integer id, String name, boolean finished) {
}

record CreateCategory(String name, boolean finished, List<CreateQuestion> questions) {
}

record Question(Integer id, String text, String answer, boolean mandatory) {
}

record CreateQuestion(String text, String answer) {
}
