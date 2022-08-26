package pt.goncalo.playground.springbootgraphql;

import com.sun.istack.NotNull;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import pt.goncalo.playground.springbootgraphql.messaging.QuizStreamController;
import pt.goncalo.playground.springbootgraphql.repository.*;
import pt.goncalo.playground.springbootgraphql.repository.entity.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
public class SpringBootGraphqlApplication {
    private static final Logger log = LoggerFactory.getLogger("SpringBootGraphqlApplication");

    public static void main(String[] args) {
        SpringApplication.run(SpringBootGraphqlApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(QuizRepository quizRepository,
                                        QuizTypeRepository quizTypeRepo,
                                        QuestionRepository questionRepository,
                                        CategoryRepository categoryRepository,
                                        CompanyRepository companyRepository) {
        return args -> {


            /**
             * Company
             */
            var createdCompany = companyRepository.saveAndFlush(new Company(UUID.randomUUID(), "my company"));
            log.info("created company: {}", createdCompany);


            var fetchedCompany = companyRepository.findById(createdCompany.getCompanyID());
            log.info("fetchedCompany company: {}", fetchedCompany);

            /**
             * INSERT QUIZ TYPE VALUES
             */
            quizTypeRepo.deleteAll();
            quizTypeRepo.saveAll(
                    List.of(
                            new QuizType(UUID.randomUUID(), "type one"),
                            new QuizType(UUID.randomUUID(), "other type")
                    )
            );
            /**
             * Quiz
             */

            quizRepository.saveAll(List.of(
                    new Quiz(UUID.randomUUID(), "Quiz Title 1", "Normal description",
                            quizTypeRepo.findAll().get(0),
                            createdCompany)
            ));

            /**
             * Category, each quiz will have two
             */

            var categories = StreamSupport
                    .stream(quizRepository.findAll().spliterator(), false)
                    .map(q -> List.of(
                            new Category(UUID.randomUUID(), q.getTitle() + " category", false, q),
                            new Category(UUID.randomUUID(), q.getTitle() + " category 2", false, q)

                    ))
                    .flatMap(Collection::stream)
                    .toList();
            categoryRepository.saveAll(categories);


            /**
             * Question, each category will have one
             */
            var questions = StreamSupport
                    .stream(categoryRepository.findAll().spliterator(), false)
                    .map(category ->
                            new Question(UUID.randomUUID(), category.getName() + " question 1", null, false, category)
                    )
                    .toList();
            questionRepository.saveAll(questions);


        };
    }

}


@Controller
class QuizController {
    private static final Logger log = LoggerFactory.getLogger("QuizController");
    private static AtomicInteger idGenerator = new AtomicInteger();


    private final QuizStreamController producer;
    private final QuizRepository quizRepository;
    private final QuizTypeRepository quizTypeRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;

    public QuizController(QuizStreamController producer, QuizRepository quizRepository, QuizTypeRepository quizTypeRepository, CompanyRepository companyRepository, CategoryRepository categoryRepository, QuestionRepository questionRepository) {
        this.producer = producer;
        this.quizRepository = quizRepository;
        this.quizTypeRepository = quizTypeRepository;
        this.companyRepository = companyRepository;
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
    }


    @QueryMapping
    Collection<Quiz> quiz() {

        return StreamSupport
                .stream(quizRepository.findAll().spliterator(), false)
                .toList();
    }

    @SubscriptionMapping
    Publisher<Quiz> onQuizChange() {
        log.info("onQuizChange called!");
        return producer.getStream();
    }


    @MutationMapping
    Quiz addQuiz(@Argument UUID companyID, @Argument CreateQuiz quiz) {
        log.info("add quiz called for {} with {}", companyID, quiz);
        Quiz q = quiz.asQuiz(
                typeId -> quizTypeRepository.findById(typeId).get(),
                cID -> companyRepository.findById(cID).get(),
                UUID.randomUUID()
        );
        return quizRepository.save(q);
    }

    @QueryMapping
    Quiz quizById(@NotNull @Argument UUID quizId) {

        return quizRepository.findById(quizId).get();
    }

    /**
     * Called when the client included categories when querying for Quiz
     *
     * @param quizzes
     * @return
     */
    @BatchMapping
    Map<Quiz, Collection<Category>> categories(List<Quiz> quizzes) {
        log.info("BatchMapping categories called.");
        var idsToFetch = quizzes
                .stream()
                .map(Quiz::getQuizId).toList();
        log.info("BatchMapping categories called for ids #[{}]", idsToFetch);


        return quizRepository
                .findAllById(idsToFetch).stream()
                .collect(Collectors.toMap(
                        quiz -> quiz,
                        quiz -> {
                            log.info("TRYING TO FIND WITH ID #{}", quiz.getQuizId());
                            var result = categoryRepository.findAllByQuiz_QuizId(quiz.getQuizId());
                            log.info("result should be : {}", result);
                            return result;
                        }
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
    Collection<Category> categories(Quiz quiz) {
        return categoryRepository.findAllByQuiz_QuizId(quiz.getQuizId());
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
    Collection<Question> questions(Category category) {
        return questionRepository.findByCategory_CategoryId(category.getCategoryId());
    }

}

@Controller
class CategoryController {

}

record CreateQuiz(String title, String description, List<CreateCategory> categories,
                  UUID quizTypeID,
                  UUID companyID) {
    /**
     * Boundary converter
     *
     * @param uuid
     * @return
     */
    Quiz asQuiz(Function<UUID, QuizType> typeSupplier,
                Function<UUID, Company> companySupplier, UUID uuid) {
        return new Quiz(uuid, title, description, typeSupplier.apply(quizTypeID), companySupplier.apply(companyID));
    }
}


record CreateCategory(String name, boolean finished, List<CreateQuestion> questions) {
}


record CreateQuestion(String text, String answer) {
}
