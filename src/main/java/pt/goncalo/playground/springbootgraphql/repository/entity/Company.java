package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
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

}
