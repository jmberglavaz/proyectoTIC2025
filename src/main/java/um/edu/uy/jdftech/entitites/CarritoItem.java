package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carrito_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    
    @Column(name = "tipo_producto", nullable = false)
    private String tipoProducto; // "PIZZA" o "HAMBURGUESA"
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;
    
    // Calcular subtotal (cantidad * precio unitario)
    public Double calcularSubtotal() {
        return precioUnitario * cantidad;
    }
    
    // Actualizar cantidad
    public void actualizarCantidad(Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = nuevaCantidad;
    }
}