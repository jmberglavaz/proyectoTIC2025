package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.List;

@Entity
@Table(name = "pizzas")
public class Pizza {

    @Id
    @GeneratedValue(generator = "pizzas_ids")
    @GenericGenerator(name = "pizzas_ids", strategy = "increment")
    @Column(name = "id_pizza")
    private Long id_pizza;

    @Column(name = "tipo_de_masa")
    private String tipo_masa;

    @Column(name = "tipo_de_salsa")
    private String tipo_salsa;

    @Column(name = "tipo_de_queso")
    private String tipo_queso;

    @Column(name = "tamanio")
    private String tamanio;

    @Column(name = "precio")
    private double precio;

//    @OneToMany(mappedBy = "pizza", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Topping> toppings;

    public Pizza() {}

    public Pizza(String tipo_masa, String tipo_salsa, String tipo_queso, String tamanio, double precio) {
        this.tipo_masa = tipo_masa;
        this.tipo_salsa = tipo_salsa;
        this.tipo_queso = tipo_queso;
        this.tamanio = tamanio;
        this.precio = precio;
    }

//    public double getPrecioTotal() {
//        double total = precio;
//        if (toppings != null) {
//            for (Topping t : toppings) total += t.getPrecioTopping();
//        }
//        return total;
//    }

}