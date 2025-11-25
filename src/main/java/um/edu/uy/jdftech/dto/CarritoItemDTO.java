package um.edu.uy.jdftech.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Representa un producto personalizado (pizza o hamburguesa) dentro del carrito.
 * Ahora incluye la configuración necesaria para reconstruir las entidades.
 */
@Getter
public class CarritoItemDTO {

    private final String id;
    private final String tipo; // "pizza" o "burger"
    private final String titulo;
    private final String descripcion;
    private int cantidad;
    private final double precioUnitario;

    // ⭐ NUEVO: Configuración estructurada para reconstruir entidades
    private final Map<String, Object> configuracion;

    // Constructor original (mantiene compatibilidad)
    public CarritoItemDTO(String tipo, String titulo, String descripcion, int cantidad, double precioUnitario) {
        this(tipo, titulo, descripcion, cantidad, precioUnitario, new HashMap<>());
    }

    // ⭐ NUEVO: Constructor con configuración
    public CarritoItemDTO(String tipo, String titulo, String descripcion, int cantidad, double precioUnitario, Map<String, Object> configuracion) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cantidad = Math.max(1, cantidad);
        this.precioUnitario = precioUnitario;
        this.configuracion = configuracion != null ? configuracion : new HashMap<>();
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad);
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    // ⭐ Métodos helper para configuración
    public String getTamanio() {
        return (String) configuracion.get("tamanio");
    }

    public Long getMasaId() {
        return (Long) configuracion.get("masaId");
    }

    public Long getSalsaId() {
        return (Long) configuracion.get("salsaId");
    }

    public Long getQuesoId() {
        return (Long) configuracion.get("quesoId");
    }

    @SuppressWarnings("unchecked")
    public List<Long> getToppingsIds() {
        return (List<Long>) configuracion.get("toppingsIds");
    }

    public Integer getCantidadCarnes() {
        return (Integer) configuracion.get("cantidadCarnes");
    }

    public Long getCarneId() {
        return (Long) configuracion.get("carneId");
    }

    public Long getPanId() {
        return (Long) configuracion.get("panId");
    }

    @SuppressWarnings("unchecked")
    public List<Long> getAderezosIds() {
        return (List<Long>) configuracion.get("aderezosIds");
    }

    @SuppressWarnings("unchecked")
    public List<Long> getExtrasIds() {
        return (List<Long>) configuracion.get("extrasIds");
    }
}