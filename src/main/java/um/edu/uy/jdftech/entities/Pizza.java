package um.edu.uy.jdftech.entities;

import jakarta.persistence.*;
import lombok.*;
import um.edu.uy.jdftech.enums.TamanioPizza;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizzas")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pizza {
    @Id
    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "PRECIO")
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "TAMAÑO")
    private TamanioPizza tamanio;

    @ManyToMany
    @JoinTable(
            name = "pizza_toppings",
            joinColumns = @JoinColumn(name = "nombre_pizza"),
            inverseJoinColumns = @JoinColumn(name = "nombre_topping")
    )
    private Set<Topping> toppings = new HashSet<>();

    public void agregarTopping(Topping topping) {
        this.toppings.add(topping);
        topping.getPizzas().add(this);
    }

    public void eliminarTopping(Topping topping) {
        this.toppings.remove(topping);
        topping.getPizzas().remove(this);
    }

}
