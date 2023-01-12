package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "dummy_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DummyEntity {
    @Id
    Integer id;
    String name;
}
