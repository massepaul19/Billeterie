package cm.billetterie.entity;

import cm.billetterie.entity.enums.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;

@Entity
@Table(name = "compte_admin")
@Getter @Setter @NoArgsConstructor
public class CompteAdmin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleCompte role;

    @Column(name = "agence_id")
    private Long agenceId;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "mot_de_passe_hash")
    private String motDePasseHash;

    @Column(name = "actif")
    private Boolean actif = true;

}
