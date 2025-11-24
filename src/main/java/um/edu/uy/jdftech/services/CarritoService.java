package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.repositories.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private HamburguesaRepository hamburguesaRepository;

    // Obtener o crear carrito para un cliente
    @Transactional
    public Carrito obtenerOCrearCarrito(Cliente cliente) {
        return carritoRepository.findByCliente(cliente)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = Carrito.builder()
                            .cliente(cliente)
                            .build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    // Obtener carrito por ID de cliente
    public Optional<Carrito> obtenerCarritoPorClienteId(Long clienteId) {
        return carritoRepository.findByClienteId(clienteId);
    }

    // Agregar producto al carrito
    @Transactional
    public CarritoItem agregarProducto(Carrito carrito, Long productoId, String tipoProducto, Integer cantidad) {
        // Verificar que el producto existe y obtener su precio
        BigDecimal precioUnitario = obtenerPrecioProducto(productoId, tipoProducto);

        // Buscar si ya existe el item en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByCarritoAndProductoIdAndTipoProducto(carrito, productoId, tipoProducto);

        if (itemExistente.isPresent()) {
            // Si existe, actualizar cantidad
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            // Si no existe, crear nuevo item
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carrito(carrito)
                    .productoId(productoId)
                    .tipoProducto(tipoProducto)
                    .cantidad(cantidad)
                    .precioUnitario(precioUnitario)
                    .build();
            carrito.agregarItem(nuevoItem);
            return carritoItemRepository.save(nuevoItem);
        }
    }

    // Actualizar cantidad de un item
    @Transactional
    public CarritoItem actualizarCantidad(Long itemId, Integer nuevaCantidad) {
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        item.setCantidad(nuevaCantidad);
        return carritoItemRepository.save(item);
    }

    // Eliminar item del carrito
    @Transactional
    public void eliminarItem(Long itemId) {
        carritoItemRepository.deleteById(itemId);
    }

    // Vaciar carrito
    @Transactional
    public void vaciarCarrito(Carrito carrito) {
        carritoItemRepository.deleteByCarrito(carrito);
        carrito.limpiar();
        carritoRepository.save(carrito);
    }

    // Calcular total del carrito
    public BigDecimal calcularTotal(Carrito carrito) {
        return carrito.calcularTotal();
    }

    // Método auxiliar para obtener precio del producto
    private BigDecimal obtenerPrecioProducto(Long productoId, String tipoProducto) {
        if ("PIZZA".equals(tipoProducto)) {
            Pizza pizza = pizzaRepository.findByIdPizza(productoId)
                    .orElseThrow(() -> new RuntimeException("Pizza no encontrada"));
            return BigDecimal.valueOf(pizza.getPrecioTotal());
        } else if ("HAMBURGUESA".equals(tipoProducto)) {
            Hamburguesa hamburguesa = hamburguesaRepository.findByIdHamburguesa(productoId)
                    .orElseThrow(() -> new RuntimeException("Hamburguesa no encontrada"));
            return BigDecimal.valueOf(hamburguesa.getPrecioTotal());
        } else {
            throw new IllegalArgumentException("Tipo de producto no válido: " + tipoProducto);
        }
    }

    private final List<um.edu.uy.jdftech.dto.CarritoItem> items = new ArrayList<>();

    public List<um.edu.uy.jdftech.dto.CarritoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(um.edu.uy.jdftech.dto.CarritoItem item) {
        items.add(item);
    }

    public void removeItem(String id) {
        items.removeIf(i -> i.getId().equals(id));
    }

    public void updateQuantity(String id, int cantidad) {
        Optional<um.edu.uy.jdftech.dto.CarritoItem> item = items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
        item.ifPresent(i -> i.setCantidad(cantidad));
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(um.edu.uy.jdftech.dto.CarritoItem::getSubtotal)
                .sum();
    }

    public void clear() {
        items.clear();
    }
}