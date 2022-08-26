package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
