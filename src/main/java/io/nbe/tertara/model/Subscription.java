package io.nbe.tertara.model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription extends AuditModel {

    @Id
    @GeneratedValue(generator = "subscription_generator")
    @SequenceGenerator(
            name = "subscription_generator",
            sequenceName = "subscription_sequence",
            initialValue = 1000
    )
    private Long id;

    @NotBlank
    @Email
    @Size(min = 1, max = 300)
    @Column(columnDefinition = "text")
    private String email;

    private boolean newsletter;


}
