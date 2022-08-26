package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.Category;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Collection<Category> findAllByQuiz_QuizId(UUID quizID);

}
