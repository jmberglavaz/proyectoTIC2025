package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.repositories.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private MedioDePagoRepository medioDePagoRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private HamburguesaRepository hamburguesaRepository;

    // Obtener direcciones del cliente
    public List<Direccion> obtenerDirecciones(Cliente cliente) {
        return direccionRepository.findByCliente(cliente);
    }

    // Obtener medios de pago del cliente
    public List<MedioDePago> obtenerMediosDePago(Cliente cliente) {
        return medioDePagoRepository.findByCliente(cliente);
    }

    // Agregar nueva dirección
    @Transactional
    public Direccion agregarDireccion(Cliente cliente, String direccion, String indicaciones) {
        Direccion nuevaDireccion = Direccion.builder()
                .direccion(direccion)
                .indicaciones(indicaciones)
                .cliente(cliente)
                .build();
        return direccionRepository.save(nuevaDireccion);
    }

    // Agregar nuevo medio de pago
    @Transactional
    public MedioDePago agregarMedioDePago(Cliente cliente, Long cardNumber, int cvv, 
                                          String firstName, String lastName, java.util.Date expirationDate) {
        MedioDePago nuevoMedioDePago = MedioDePago.builder()
                .cardNumber(cardNumber)
                .cvv(cvv)
                .firstNameOnCard(firstName)
                .lastNameOnCard(lastName)
                .expirationDate(expirationDate)
                .cliente(cliente)
                .build();
        return medioDePagoRepository.save(nuevoMedioDePago);
    }

    // Validar CVV de una tarjeta
    public boolean validarCVV(Long cardNumber, int cvvIngresado) {
        MedioDePago medioDePago = medioDePagoRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));
        return medioDePago.getCvv() == cvvIngresado;
    }

    // Crear pedido desde el carrito
    @Transactional
    public Pedido crearPedido(Cliente cliente, Long direccionId, Long cardNumber, int cvv) {
        // Validar CVV
        if (!validarCVV(cardNumber, cvv)) {
            throw new RuntimeException("CVV incorrecto");
        }

        // Obtener carrito
        Carrito carrito = carritoRepository.findByCliente(cliente)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Obtener dirección y medio de pago
        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        
        MedioDePago medioDePago = medioDePagoRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Medio de pago no encontrado"));

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .client(cliente)
                .direccion(direccion)
                .medioDePago(medioDePago)
                .date(LocalDateTime.now())
                .status(EstadoPedido.EN_COLA)
                .totalCost(0.0)
                .build();

        // Agregar productos del carrito al pedido
        for (CarritoItem item : carrito.getItems()) {
            if ("PIZZA".equals(item.getTipoProducto())) {
                Pizza pizza = pizzaRepository.findByIdPizza(item.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Pizza no encontrada"));
                pedido.getPizzas().add(pizza);
            } else if ("HAMBURGUESA".equals(item.getTipoProducto())) {
                Hamburguesa hamburguesa = hamburguesaRepository.findByIdHamburguesa(item.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Hamburguesa no encontrada"));
                pedido.getHamburguesas().add(hamburguesa);
            }
        }

        // Calcular total
        pedido.calculateTotal();

        // Guardar pedido
        pedido = pedidoRepository.save(pedido);

        // Vaciar carrito
        carritoItemRepository.deleteByCarrito(carrito);
        carrito.limpiar();
        carritoRepository.save(carrito);

        return pedido;
    }
}