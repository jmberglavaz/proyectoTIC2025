package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import um.edu.uy.jdftech.entitites.tablasIntermedias.PizzaTopping;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pizzas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

    @Id
    @GeneratedValue(generator = "pizzas_ids")
    @GenericGenerator(name = "pizzas_ids", strategy = "increment")
    @Column(name = "id_pizza")
    private Long id_pizza;

    @Column(name = "tamanio")
    private String tamanio;

    @OneToMany(mappedBy = "pizza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PizzaTopping> pizzaToppings = new ArrayList<>();

    public void agregarTopping(Topping topping, int cantidad) {
        PizzaTopping pt = new PizzaTopping(null, this, topping, cantidad);
        pizzaToppings.add(pt);
    }

    public double getPrecioTotal() {
        double total = 0;

        for (PizzaTopping pt : pizzaToppings) {
            total += pt.getTopping().getPrecioTopping() * pt.getCantidad();
        }

        return total;
    }

}