package pt.goncalo.playground.springbootgraphql.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "quiz_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizType {
    @Id
    @Column(name = "quiz_type_id", nullable = false, columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID quizTypeId;
    private String name;





}
