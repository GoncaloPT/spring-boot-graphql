package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.Quiz;

import java.util.UUID;

/**
 * THIS IS NOT INTENDED TO BE A @GraphQlRepository.
 * The idea is to showcase that @GraphQlRepository is not mandatory.
 */
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
}
