package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "toppings")
public class Topping {

    @Id
    @GeneratedValue(generator = "toppings_ids")
    @GenericGenerator(name = "toppings_ids", strategy = "increment")
    @Column(name = "id_topping")
    private Long id_topping;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio_topping")
    private double precio_topping;

    @ManyToOne
    @JoinColumn(name = "id_pizza")
    private Pizza pizza;

    @ManyToOne
    @JoinColumn(name = "id_hamburguesa")
    private Hamburguesa hamburguesa;

    public Topping() {}

    public Topping(String nombre, int cantidad, double precio_topping) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio_topping = precio_topping;
    }

    public double getPrecioTopping() {
        return precio_topping * cantidad;
    }

}