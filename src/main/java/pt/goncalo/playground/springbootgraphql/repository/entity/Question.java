package pt.goncalo.playground.springbootgraphql.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Question {
    @Id
    @Column(name = "question_id", nullable = false)
    private UUID id;
    private String text;
    private String answer;
    private boolean mandatory;


    @ManyToOne
    @JoinColumn(name = "category_id",  referencedColumnName = "category_id", columnDefinition = "uuid")
    private Category category;

}
