package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.lang.NonNull;
import pt.goncalo.playground.springbootgraphql.repository.entity.Category;
import pt.goncalo.playground.springbootgraphql.repository.entity.Question;

import java.util.Collection;
import java.util.UUID;

@GraphQlRepository(typeName = "Question")
public interface QuestionRepository extends JpaRepository<Question, UUID>,
        QuerydslPredicateExecutor<Question> {
    Collection<Question> findByCategory_CategoryId(@NonNull UUID id);


}
