package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "synonyme_recherche")
@Getter @Setter @NoArgsConstructor
public class SynonymeRecherche {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mot")
    private String mot;

    @Column(name = "critere")
    private String critere;

}
