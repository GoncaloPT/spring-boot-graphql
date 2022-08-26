package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.graphql.data.GraphQlRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.QuizType;

import java.util.UUID;

@GraphQlRepository
public interface QuizTypeRepository extends JpaRepository<QuizType, UUID>, QueryByExampleExecutor<QuizType> {
}
