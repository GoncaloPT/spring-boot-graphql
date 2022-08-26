package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Quiz {
    @Id
    @Column(name = "quiz_id", nullable = false, columnDefinition = "uuid")
    private UUID quizId;
    private String title;
    private String description;

    @OneToOne
    @JoinColumn(name = "quiz_type_id",  referencedColumnName = "quiz_type_id", nullable = false)
    private QuizType type;


    @ManyToOne
    @JoinColumn(name = "company_id",  referencedColumnName = "company_id", nullable = false)
    private Company company;

}