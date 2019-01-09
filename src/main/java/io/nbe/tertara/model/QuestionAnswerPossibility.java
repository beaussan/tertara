package io.nbe.tertara.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "questions_answer_possibility")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerPossibility extends AuditModel {

    @Id
    @GeneratedValue(generator = "questions_answer_possibility_generator")
    @SequenceGenerator(
            name = "questions_answer_possibility_generator",
            sequenceName = "questions_answer_possibility_sequence",
            initialValue = 2000
    )
    private Long id;

    @Column()
    private String value;
}
