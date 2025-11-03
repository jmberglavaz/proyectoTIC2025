package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "aderezos")
@Getter
@Setter
public class Aderezo {

    @Id
    @GeneratedValue(generator = "aderezos_ids")
    @GenericGenerator(name = "aderezos_ids", strategy = "increment")
    @Column(name = "id_aderezo")
    private Long id_aderezo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio")
    private double precio;

    @ManyToOne
    @JoinColumn(name = "id_hamburguesa")
    private Hamburguesa hamburguesa;

    public Aderezo() {
    }

    public Aderezo(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getIdAderezo() {
        return id_aderezo;
    }

    public void setIdAderezo(Long id_aderezo) {
        this.id_aderezo = id_aderezo;
    }
}