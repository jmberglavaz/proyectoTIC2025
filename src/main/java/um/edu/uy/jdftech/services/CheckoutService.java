package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.uy.jdftech.dto.CarritoItemDTO;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.repositories.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {

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

    @Autowired
    private CarritoConverter carritoConverter;  // ⭐ NUEVO

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
    public Direccion agregarDireccion(Cliente cliente, String direccion, String indicaciones, String alias) {
        if (alias == null) {
            alias = direccion;
        }
        Direccion nuevaDireccion = Direccion.builder()
                .address(direccion)
                .indications(indicaciones)
                .alias(alias).build();
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

    /**
     * ⭐ NUEVO: Crear pedido desde items en memoria (List<CarritoItemDTO>)
     */
    @Transactional
    public Pedido crearPedido(Cliente cliente, Long direccionId, Long cardNumber, int cvv,
                              List<CarritoItemDTO> items) {

        System.out.println("🔵 CheckoutService.crearPedido - Iniciando");

        // Validar CVV
        if (!validarCVV(cardNumber, cvv)) {
            throw new RuntimeException("CVV incorrecto");
        }

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("No hay items en el carrito");
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

        System.out.println("🔵 Procesando " + items.size() + " items del carrito");

        // ⭐ CONVERTIR CarritoItemDTO → Pizza/Hamburguesa y agregar al pedido
        for (CarritoItemDTO dto : items) {
            System.out.println("🔹 Procesando item: " + dto.getTipo() + " - cantidad: " + dto.getCantidad());

            if ("pizza".equals(dto.getTipo())) {
                // Crear una Pizza por cada cantidad
                for (int i = 0; i < dto.getCantidad(); i++) {
                    Pizza pizza = carritoConverter.convertirAPizza(dto);
                    pizza = pizzaRepository.save(pizza);  // Guardar primero
                    pedido.getPizzas().add(pizza);
                    System.out.println("✅ Pizza agregada al pedido - ID: " + pizza.getId_pizza());
                }
            }
            else if ("burger".equals(dto.getTipo())) {
                // Crear una Hamburguesa por cada cantidad
                for (int i = 0; i < dto.getCantidad(); i++) {
                    Hamburguesa hamburguesa = carritoConverter.convertirAHamburguesa(dto);
                    hamburguesa = hamburguesaRepository.save(hamburguesa);  // Guardar primero
                    pedido.getHamburguesas().add(hamburguesa);
                    System.out.println("✅ Hamburguesa agregada al pedido - ID: " + hamburguesa.getId_hamburguesa());
                }
            }
            else {
                System.out.println("⚠️ Tipo de producto desconocido: " + dto.getTipo());
            }
        }

        // Calcular total
        pedido.calculateTotal();
        System.out.println("💰 Total del pedido: $" + pedido.getTotalCost());

        // Guardar pedido
        pedido = pedidoRepository.save(pedido);
        System.out.println("✅ Pedido guardado - ID: " + pedido.getId());

        return pedido;
    }
}