package fructose.model.evaluation;

import fructose.model.enumerator.ReponseEvaluation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "section")
public class CritereEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", nullable = false)
    private String question;

    @Enumerated(EnumType.STRING)
    private ReponseEvaluation reponse; // Enum pour les réponses

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SectionEvaluation section;
}
