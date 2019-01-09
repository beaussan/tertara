package io.nbe.tertara.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "answers")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Answer extends AuditModel {
    @Id
    @GeneratedValue(generator = "answer_generator")
    @SequenceGenerator(
            name = "answer_generator",
            sequenceName = "answer_sequence",
            initialValue = 1000
    )
    private Long id;

    @Column(columnDefinition = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private QuestionAnswerPossibility answerValue;

}
