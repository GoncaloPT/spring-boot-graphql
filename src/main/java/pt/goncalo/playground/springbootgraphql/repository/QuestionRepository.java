package pt.goncalo.playground.springbootgraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import pt.goncalo.playground.springbootgraphql.repository.entity.Question;

import java.util.Collection;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    Collection<Question> findByCategory_CategoryId(@NonNull UUID id);


}
