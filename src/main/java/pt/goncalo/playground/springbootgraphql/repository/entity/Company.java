package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
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
