package pt.goncalo.playground.springbootgraphql.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.goncalo.playground.springbootgraphql.repository.CategoryRepository;
import pt.goncalo.playground.springbootgraphql.repository.CompanyRepository;
import pt.goncalo.playground.springbootgraphql.repository.QuizRepository;
import pt.goncalo.playground.springbootgraphql.repository.QuizTypeRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.Quiz;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

@Component
public class QuizStreamController {
    private Semaphore generationMutex = new Semaphore(1);
    private static final Logger log = LoggerFactory.getLogger("QuizProducer");
    private FluxSink<Quiz> sink;
    private ConnectableFlux<Quiz> stream;
    private final CompanyRepository compRepo;
    private final QuizTypeRepository quizTypeRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    public QuizStreamController(CompanyRepository compRepo, QuizTypeRepository quizTypeRepository, CategoryRepository categoryRepository, QuizRepository quizRepository) {
        this.compRepo = compRepo;
        this.quizTypeRepository = quizTypeRepository;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("postConstruct called");
        Flux<Quiz> innerPublisher = Flux.create(emitter -> sink = emitter);
        stream = innerPublisher.publish();
        stream.connect();


    }

    /**
     * Just picks already existing quiz and sends them to the Sink.
     */
    private void generateSomeQuizzesFromDatabase() {
        var r = new Random(10);
        try {
            generationMutex.acquire();
            Flux
                    .fromStream(
                            Stream.generate(quizRepository::findAll)
                                    .flatMap(Collection::stream)
                    )
                    .map(quiz -> {
                        quiz.setTitle("title " + r.nextInt());
                        return quiz;
                    })
                    .doFinally((c) -> generationMutex.release())
                    .delayElements(Duration.ofSeconds(1)).take(10)
                    .subscribe(quiz -> {
                        log.info("generated quiz {}", quiz);
                        sink.next(quiz);
                    });
        } catch (InterruptedException ie) {
            log.warn("generation was interrupted ", ie);
            Thread.currentThread().interrupt();
        }
    }

    public ConnectableFlux<Quiz> getStream() {
        generateSomeQuizzesFromDatabase();
        return stream;
    }

    public FluxSink<Quiz> sink() {
        return sink;
    }
}
