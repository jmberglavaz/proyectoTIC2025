package um.edu.uy.jdftech.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.uy.jdftech.dto.CarritoItemDTO;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.repositories.*;

import java.util.ArrayList;
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
        Double precioUnitario = obtenerPrecioProducto(productoId, tipoProducto);

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
    public Double calcularTotal(Carrito carrito) {
        return carrito.calcularTotal();
    }

    // Método auxiliar para obtener precio del producto
    private Double obtenerPrecioProducto(Long productoId, String tipoProducto) {
        if ("PIZZA".equals(tipoProducto)) {
            Pizza pizza = pizzaRepository.findByIdPizza(productoId)
                    .orElseThrow(() -> new RuntimeException("Pizza no encontrada"));
            return pizza.getPrecioTotal();
        } else if ("HAMBURGUESA".equals(tipoProducto)) {
            Hamburguesa hamburguesa = hamburguesaRepository.findByIdHamburguesa(productoId)
                    .orElseThrow(() -> new RuntimeException("Hamburguesa no encontrada"));
            return hamburguesa.getPrecioTotal();
        } else {
            throw new IllegalArgumentException("Tipo de producto no válido: " + tipoProducto);
        }
    }

    // Retorna la lista de items en memoria
    @Getter
    private final List<CarritoItemDTO> items = new ArrayList<>();

    public void addItem(CarritoItemDTO item) {
        items.add(item);
    }

    public void removeItem(String id) {
        items.removeIf(i -> i.getId().equals(id));
    }

    public void updateQuantity(String id, int cantidad) {
        Optional<CarritoItemDTO> item = items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
        item.ifPresent(i -> i.setCantidad(cantidad));
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(CarritoItemDTO::getSubtotal)
                .sum();
    }

    public void clear() {
        items.clear();
        System.out.println("🧹 Carrito en memoria limpiado");

    }
}