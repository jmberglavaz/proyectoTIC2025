package um.edu.uy.jdftech.dto;

import java.util.UUID;

/**
 * Representa un producto personalizado (pizza o hamburguesa) dentro del carrito.
 * Es un modelo simple para la capa web, independiente de las entidades JPA.
 */
public class CarritoItem {

    private final String id;
    private final String tipo;
    private final String titulo;
    private final String descripcion;
    private int cantidad;
    private final double precioUnitario;

    public CarritoItem(String tipo, String titulo, String descripcion, int cantidad, double precioUnitario) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cantidad = Math.max(1, cantidad);
        this.precioUnitario = precioUnitario;
    }

    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad);
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }
}
