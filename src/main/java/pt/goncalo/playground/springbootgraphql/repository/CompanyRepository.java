package pt.goncalo.playground.springbootgraphql.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;
import pt.goncalo.playground.springbootgraphql.repository.entity.Company;

import java.util.UUID;

@GraphQlRepository(typeName = "Company")
public interface CompanyRepository extends JpaRepository<Company, UUID>, QuerydslPredicateExecutor<Company> {

}
