package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "toppings")
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topping {

    @Id
    @GeneratedValue(generator = "toppings_ids")
    @GenericGenerator(name = "toppings_ids", strategy = "increment")
    @Column(name = "id_topping")
    private Long idTopping;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "hamburguesa_o_pizza")
    private char hamburguesaOPizza;

    @Column(name = "tipo_de_topping")
    private char tipo;

    @Column(name = "precio_topping")
    private double precioTopping;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;
}