package um.edu.uy.jdftech.entitites.tablasIntermedias;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.edu.uy.jdftech.entitites.Pizza;
import um.edu.uy.jdftech.entitites.Topping;

@Entity
@Table(name = "pizza_topping")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PizzaTopping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pizza")
    private Pizza pizza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTopping")
    private Topping topping;

    private Integer cantidad;
}