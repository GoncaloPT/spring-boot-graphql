package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.QuizType;

import java.util.UUID;

@GraphQlRepository(typeName = "QuizType")
public interface QuizTypeRepository extends JpaRepository<QuizType, UUID>, QuerydslPredicateExecutor<QuizType> {
}
