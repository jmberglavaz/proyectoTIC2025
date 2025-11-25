package um.edu.uy.jdftech.entitites.tablasIntermedias;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.edu.uy.jdftech.entitites.Aderezo;
import um.edu.uy.jdftech.entitites.Hamburguesa;

@Entity
@Table(name = "hamburguesa_aderezo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HamburguesaAderezo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hamburguesa")
    private Hamburguesa hamburguesa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idAderezo")
    private Aderezo aderezo;

    private Integer cantidad;
}
