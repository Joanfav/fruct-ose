package fructose.model.evaluation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "evaluationEmployeur" + "evaluationMilieuStage")
public class SectionEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false)
    private String name;

    @Column(name = "commentaire", nullable = false)
    private String commentaire;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CritereEvaluation> criteres;

    @ManyToOne
    @JoinColumn(name = "evaluation_employeur_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private EvaluationEmployeur evaluationEmployeur;

    @ManyToOne
    @JoinColumn(name = "evaluation_milieu_stage_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private EvaluationMilieuStage evaluationMilieuStage;

    public void addCritere(CritereEvaluation critere) {
        this.criteres.add(critere);
    }
}
