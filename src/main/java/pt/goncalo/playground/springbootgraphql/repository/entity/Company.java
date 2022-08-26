package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Company {
    @Id()
    @Column(name = "company_id", nullable = false, columnDefinition = "uuid"    )
    private UUID companyID;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        return companyID.equals(company.companyID);
    }

    @Override
    public int hashCode() {
        return companyID.hashCode();
    }
}
