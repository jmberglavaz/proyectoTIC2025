package um.edu.uy.jdftech.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "toppings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Topping {
    @Id
    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "PRECIO")
    private Double precio;

    @ManyToMany(mappedBy = "toppings")
    private Set<Pizza> pizzas  = new HashSet<>();
}
