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
    @Column(name = "ID")
    private Long id;

    @Column(name = "DIRECCION", nullable = false)
    private String direccion; // texto simple: "18 de julio 1234"

    @Column(name = "INDICACIONES")
    private String indicaciones; // opcional: "timbre roto", "portón verde"

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}