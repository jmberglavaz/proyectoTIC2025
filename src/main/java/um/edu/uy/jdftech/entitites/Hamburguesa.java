package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import um.edu.uy.jdftech.entitites.tablasIntermedias.HamburguesaAderezo;
import um.edu.uy.jdftech.entitites.tablasIntermedias.HamburguesaTopping;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hamburguesas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private List<HamburguesaAderezo> hamburguesaAderezos = new ArrayList<>();

    @OneToMany(mappedBy = "hamburguesa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HamburguesaTopping> hamburguesaToppings = new ArrayList<>();

    // Métodos para manejar toppings

    public void agregarTopping(Topping topping, int cantidad) {
        HamburguesaTopping ht = new HamburguesaTopping(null, this, topping, cantidad);
        hamburguesaToppings.add(ht);
    }

    public void agregarAderezo(Aderezo aderezo, int cantidad) {
        HamburguesaAderezo ha = new HamburguesaAderezo(null, this, aderezo, cantidad);
        hamburguesaAderezos.add(ha);
    }

    public double getPrecioTotal() {
        double total = precio_base;

        for (HamburguesaTopping ht : hamburguesaToppings) {
            total += ht.getTopping().getPrecioTopping() * ht.getCantidad();
        }

        for (HamburguesaAderezo ha : hamburguesaAderezos) {
            total += ha.getAderezo().getPrecio() * ha.getCantidad();
        }

        return total;
    }


}