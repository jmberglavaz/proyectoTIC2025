package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STREET_NAME", nullable = false)
    private String streetName;

    @Column(name = "DOOR_NUMBER", nullable = false)
    private String doorNumber;

    @Column(name = "INDICATIONS")
    private String indications;

    @Column(name = "ALIAS")
    private String alias;

    @Column(name = "IS_DEFECT")
    private boolean isDefect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;
}
