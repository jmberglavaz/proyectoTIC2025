package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hamburguesas")
public class Hamburguesa {

    @Id
    @GeneratedValue(generator = "hamburguesas_ids")
    @GenericGenerator(name = "hamburguesas_ids", strategy = "increment")
    @Column(name = "id_hamburguesa")
    private Long id_hamburguesa;

    @Column(name = "cant_de_carnes")
    private int cant_de_carnes;

    @Column(name = "precio_base")
    private double precio_base;
    @OneToMany(mappedBy = "hamburguesa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aderezo> aderezos;

    @OneToMany(mappedBy = "hamburguesa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Topping> toppings;

    public Hamburguesa() {
    }

    public Hamburguesa(int cant_de_carnes, double precio_base) {
        this.cant_de_carnes = cant_de_carnes;
        this.precio_base = precio_base;
        this.aderezos = new ArrayList<>();
        this.toppings = new ArrayList<>();
    }

    // Calcular precio total con aderezos y toppings
    public double getPrecioTotal() {
        double total = precio_base;
        if (aderezos != null) {
            for (Aderezo a : aderezos) total += a.getPrecio();
        }
        if (toppings != null) {
            for (Topping t : toppings) total += t.getPrecioTopping();
        }
        return total;
    }
}