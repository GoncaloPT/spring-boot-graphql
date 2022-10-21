package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.GraphQlRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.Category;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@GraphQlRepository(typeName = "Category")
public interface CategoryRepository extends JpaRepository<Category, UUID>, QuerydslPredicateExecutor<Category> {
    Collection<Category> findAllByQuiz_QuizId(UUID quizID);



}
