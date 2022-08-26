package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {
    @Id
    @Column(name = "category_id", nullable = false, columnDefinition = "uuid")
    private UUID categoryId;
    private String name;
    private boolean finished;

    @ManyToOne
    @JoinColumn(name = "quiz_id",  referencedColumnName = "quiz_id")
    private Quiz quiz;

}
