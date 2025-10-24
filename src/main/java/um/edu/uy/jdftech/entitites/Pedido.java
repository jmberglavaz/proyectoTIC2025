package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;
import um.edu.uy.jdftech.enums.EstadoPedido;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "card_number", nullable = false)
    private MedioDePago medioDePago;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "DATE", nullable = false)
    private LocalDateTime date;

    @Column(name = "STATUS")
    private EstadoPedido status;
    // por defecto en cola

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pedido_pizza",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Pizza> pizzas = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pedido_hamburguesa",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "hamburguesa_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Hamburguesa> hamburguesas = new HashSet<>();


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pedido_acompanamiento",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "acompanamiento_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Acompanamiento> acompanamientos = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pedido_bebida",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "bebida_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Bebida> bebidas = new HashSet<>();

    @Column(name = "TOTAL_COST")
    private Double totalCost;

    public Pedido(Cliente cliente) { // no le paso dirección xq las tendría que buscar entre las que el cliente ya tiene, o darle la opción de agregar otra
        this.client = cliente;
        this.date = LocalDateTime.now();
        // Falta agregar medio de pago
        this.totalCost = 0.0;
        this.pizzas = new HashSet<>();
        this.hamburguesas = new HashSet<>();
        this.acompanamientos = new HashSet<>();
        this.bebidas = new HashSet<>();
    }

    public void calculateTotal() {
        this.totalCost = bebidas.stream().mapToDouble(Bebida::getPrice).sum() + acompanamientos.stream().mapToDouble(Acompanamiento::getPrice).sum(); // agregar pizza y hamburguesa
    }
}
