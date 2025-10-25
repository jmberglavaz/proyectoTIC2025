package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bebidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Bebida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private Double price;

    @ManyToMany(mappedBy = "bebidas")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Pedido> pedidos = new HashSet<>();
}
