package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Cliente cliente;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CarritoItem> items = new ArrayList<>();
    
    // Método helper para agregar items
    public void agregarItem(CarritoItem item) {
        items.add(item);
        item.setCarrito(this);
    }
    
    // Método helper para remover items
    public void removerItem(CarritoItem item) {
        items.remove(item);
        item.setCarrito(null);
    }
    
    // Calcular total del carrito
    public Double calcularTotal() {
        return items.stream()
                .mapToDouble(CarritoItem::calcularSubtotal)
                .sum();
    }
    
    // Limpiar carrito
    public void limpiar() {
        items.clear();
    }
}