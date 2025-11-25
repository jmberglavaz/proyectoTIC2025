package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.dto.TicketDGIDTO;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.entitites.tablasIntermedias.HamburguesaAderezo;
import um.edu.uy.jdftech.entitites.tablasIntermedias.HamburguesaTopping;
import um.edu.uy.jdftech.entitites.tablasIntermedias.PizzaTopping;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.repositories.AcompanamientoRepository;
import um.edu.uy.jdftech.repositories.BebidaRepository;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.repositories.PedidoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final BebidaRepository bebidaRepository;
    private final AcompanamientoRepository acompanamientoRepository;
    // falta agregar el resto

    public Pedido create(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntityNotFoundException("El cliente no existe"));
        Pedido pedido = new Pedido(cliente);
        return pedidoRepository.save(pedido);
    }

    public Pedido addDrink(Long pedidoId, Long bebidaId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
        ;
        Bebida bebida = bebidaRepository.findById(bebidaId).orElseThrow(() -> new EntityNotFoundException("Bebida no pudo ser agregada"));
        pedido.getBebidas().add(bebida);
        bebida.getPedidos().add(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido removeDrink(Long pedidoId, Long bebidaId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
        ;
        Bebida bebida = bebidaRepository.findById(bebidaId).orElseThrow(() -> new EntityNotFoundException("Bebida no fue encontrada para eliminarla"));
        pedido.getBebidas().remove(bebida);
        bebida.getPedidos().remove(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido agregarAcompanamiento(Long pedidoId, Long acompanamientoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
        ;
        Acompanamiento acompanamiento = acompanamientoRepository.findById(acompanamientoId).orElseThrow(() -> new EntityNotFoundException("Acompañamiento no pudo ser agregado"));
        pedido.getAcompanamientos().add(acompanamiento);
        acompanamiento.getPedidos().add(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido removeAcompanamiento(Long pedidoId, Long acompanamientoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
        ;
        Acompanamiento acompanamiento = acompanamientoRepository.findById(acompanamientoId).orElseThrow(() -> new EntityNotFoundException("Acompañamiento no fue encontrado para eliminarla"));
        pedido.getBebidas().remove(acompanamiento);
        acompanamiento.getPedidos().remove(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> findByClienteId(Long clientId) {
        return pedidoRepository.findByClientId(clientId);
    }

    public List<Pedido> getLast3OrdersByClient(Long clienteId) {
        Pageable pageable = PageRequest.of(0, 3);
        return pedidoRepository.getLast3OrdersByClient(clienteId, pageable);
    }

    public List<Pedido> findHistoricByClient(Long clienteId, LocalDateTime from, LocalDateTime to) {
        return pedidoRepository.findHistoricByClient(clienteId, from, to);
    }

    // Obtener pedido por ID
    public Pedido obtenerPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
    }

    // Cambiar estado del pedido
    public Pedido cambiarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        pedido.setStatus(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    // Cancelar pedido (solo si está EN_COLA)
    public Pedido cancelarPedido(Long pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);

        if (pedido.getStatus() != EstadoPedido.EN_COLA) {
            throw new RuntimeException("Solo se pueden cancelar pedidos en cola");
        }

        pedido.setStatus(EstadoPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }


    public List<Pedido> findPedidosActivos() {
        try {
            return pedidoRepository.findByStatusIn(List.of(
                    EstadoPedido.EN_COLA,
                    EstadoPedido.EN_PREPARACION,
                    EstadoPedido.EN_CAMINO
            ));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    // Obtener todos los pedidos de un cliente
    public List<Pedido> obtenerPedidosCliente(Cliente cliente) {
        return pedidoRepository.findByClientIdOrderByDateDesc(cliente.getId());
    }

    public List<Pedido> findWithFilters(String numero, String clienteId, String estado,
                                        String tipo, String desde, String hasta) {
        System.out.println("=== FIND WITH FILTERS ===");
        System.out.println("numero: " + numero + ", clienteId: " + clienteId + ", estado: " + estado);

        try {
            // Convertir parámetros
            Long numeroLong = null;
            if (numero != null && !numero.trim().isEmpty()) {
                try {
                    numeroLong = Long.parseLong(numero.replace("#", "").trim());
                } catch (NumberFormatException e) {
                    // Si el número no es válido, retornar lista vacía
                    return Collections.emptyList();
                }
            }

            Long clienteIdLong = null;
            if (clienteId != null && !clienteId.trim().isEmpty()) {
                try {
                    clienteIdLong = Long.parseLong(clienteId.trim());
                } catch (NumberFormatException e) {
                    // Si el ID de cliente no es válido, retornar lista vacía
                    return Collections.emptyList();
                }
            }

            EstadoPedido estadoEnum = null;
            if (estado != null && !estado.trim().isEmpty()) {
                try {
                    estadoEnum = EstadoPedido.valueOf(estado);
                } catch (IllegalArgumentException e) {
                    // Si el estado no es válido, retornar lista vacía
                    return Collections.emptyList();
                }
            }

            LocalDateTime desdeDate = null;
            if (desde != null && !desde.trim().isEmpty()) {
                try {
                    desdeDate = LocalDateTime.parse(desde + "T00:00:00");
                } catch (Exception e) {
                    // Si la fecha no es válida, ignorar el filtro
                }
            }

            LocalDateTime hastaDate = null;
            if (hasta != null && !hasta.trim().isEmpty()) {
                try {
                    hastaDate = LocalDateTime.parse(hasta + "T23:59:59");
                } catch (Exception e) {
                    // Si la fecha no es válida, ignorar el filtro
                }
            }

            // Aplicar filtro de tipo (activo/inactivo)
            if (tipo != null && !tipo.trim().isEmpty()) {
                List<EstadoPedido> estados;
                if ("activo".equals(tipo)) {
                    estados = List.of(EstadoPedido.EN_COLA, EstadoPedido.EN_PREPARACION, EstadoPedido.EN_CAMINO);
                } else if ("inactivo".equals(tipo)) {
                    estados = List.of(EstadoPedido.ENTREGADO);
                } else {
                    estados = List.of();
                }

                return pedidoRepository.findActivosWithFilters(estados, numeroLong, clienteIdLong, desdeDate, hastaDate);
            } else {
                return pedidoRepository.findWithFilters(numeroLong, clienteIdLong, estadoEnum, desdeDate, hastaDate);
            }

        } catch (Exception e) {
            // En caso de cualquier otro error, retornar lista vacía silenciosamente
            System.err.println("Error al aplicar filtros: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Pedido> findLast10Orders() {
        Pageable pageable = PageRequest.of(0, 10);
        return pedidoRepository.findLast10Orders(pageable);
    }

    public Pedido updateStatus(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
        pedido.setStatus(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    public List<Cliente> findByFullName(String firstName, String lastName) {
        return clienteRepository.findByFullNameContaining(firstName + " " + lastName);
    }

    // Obtener el pedido activo más reciente de un cliente
    public Optional<Pedido> obtenerPedidoActivo(Cliente cliente) {
        List<Pedido> pedidosActivos = pedidoRepository.findPedidosActivosByClientId(cliente.getId());
        return pedidosActivos.isEmpty() ? Optional.empty() : Optional.of(pedidosActivos.get(0));
    }

    /**
     * Servicio para DGI - Obtener todos los tickets/pedidos de una fecha específica
     */
    public List<TicketDGIDTO> obtenerTicketsPorFecha(LocalDateTime fecha) {
        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = fecha.toLocalDate().atTime(23, 59, 59);

        List<Pedido> pedidos = pedidoRepository.findByDateBetween(inicioDia, finDia);

        return pedidos.stream()
                .map(this::convertirATicketDGI)
                .collect(Collectors.toList());
    }

    /**
     * Servicio para DGI - Obtener tickets por rango de fechas
     */
    public List<TicketDGIDTO> obtenerTicketsPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        List<Pedido> pedidos = pedidoRepository.findByDateBetween(desde, hasta);

        return pedidos.stream()
                .map(this::convertirATicketDGI)
                .collect(Collectors.toList());
    }

    private TicketDGIDTO convertirATicketDGI(Pedido pedido) {
        List<String> items = new ArrayList<>();

        // Agregar pizzas
        if (pedido.getPizzas() != null) {
            pedido.getPizzas().forEach(pizza ->
                    items.add("Pizza: " + pizza.getClass().getSimpleName())
            );
        }

        // Agregar hamburguesas
        if (pedido.getHamburguesas() != null) {
            pedido.getHamburguesas().forEach(hamburguesa ->
                    items.add("Hamburguesa: " + hamburguesa.getClass().getSimpleName())
            );
        }

        // Agregar bebidas
        if (pedido.getBebidas() != null) {
            pedido.getBebidas().forEach(bebida ->
                    items.add("Bebida: " + bebida.getName() + " (" + bebida.getSize() + ")")
            );
        }

        // Agregar acompañamientos
        if (pedido.getAcompanamientos() != null) {
            pedido.getAcompanamientos().forEach(acompanamiento ->
                    items.add("Acompañamiento: " + acompanamiento.getName() + " (" + acompanamiento.getSize() + ")")
            );
        }

        return TicketDGIDTO.builder()
                .id(pedido.getId())
                .fecha(pedido.getDate())
                .clienteNombre(pedido.getClient().getFirstName() + " " + pedido.getClient().getLastName())
                .clienteCedula(pedido.getClient().getId().toString())
                .clienteEmail(pedido.getClient().getEmail())
                .total(pedido.getTotalCost())
                .estado(pedido.getStatus())
                .items(items)
                .build();
    }

    public List<Pedido> findUltimosPedidosByCliente(Long clienteId, int limite) {
        return pedidoRepository.findByClientIdOrderByDateDesc(clienteId)
                .stream()
                .limit(limite)
                .collect(Collectors.toList());
    }

    public Pedido obtenerPedidoConDetalles(Long pedidoId) {
        Pedido pedido = pedidoRepository.findByIdWithDetails(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));

        // Forzar carga de TODAS las relaciones LAZY
        if (pedido.getPizzas() != null) {
            pedido.getPizzas().size(); // Fuerza carga de pizzas

            for (Pizza pizza : pedido.getPizzas()) {
                if (pizza.getPizzaToppings() != null) {
                    pizza.getPizzaToppings().size(); // Fuerza carga de toppings de pizza
                    for (PizzaTopping pt : pizza.getPizzaToppings()) {
                        if (pt.getTopping() != null) {
                            pt.getTopping().getNombre(); // Fuerza carga del topping
                        }
                    }
                }
            }
        }

        if (pedido.getHamburguesas() != null) {
            pedido.getHamburguesas().size(); // Fuerza carga de hamburguesas

            for (Hamburguesa hamburguesa : pedido.getHamburguesas()) {
                if (hamburguesa.getHamburguesaToppings() != null) {
                    hamburguesa.getHamburguesaToppings().size(); // Fuerza carga de toppings
                    for (HamburguesaTopping ht : hamburguesa.getHamburguesaToppings()) {
                        if (ht.getTopping() != null) {
                            ht.getTopping().getNombre(); // Fuerza carga del topping
                        }
                    }
                }

                if (hamburguesa.getHamburguesaAderezos() != null) {
                    hamburguesa.getHamburguesaAderezos().size(); // Fuerza carga de aderezos
                    for (HamburguesaAderezo ha : hamburguesa.getHamburguesaAderezos()) {
                        if (ha.getAderezo() != null) {
                            ha.getAderezo().getNombre(); // Fuerza carga del aderezo
                        }
                    }
                }
            }
        }

        // Fuerza carga de bebidas y acompañamientos
        if (pedido.getBebidas() != null) {
            pedido.getBebidas().size();
        }

        if (pedido.getAcompanamientos() != null) {
            pedido.getAcompanamientos().size();
        }

        return pedido;
    }

    public List<Pedido> findUltimosPedidosByClienteWithDetails(Long clienteId, int limite) {
        List<Pedido> pedidos = pedidoRepository.findByClientIdOrderByDateDesc(clienteId)
                .stream()
                .limit(limite)
                .collect(Collectors.toList());

        // Forzar carga de relaciones para cada pedido
        for (Pedido pedido : pedidos) {
            // Usar el método existente que carga todas las relaciones
            Pedido pedidoConDetalles = obtenerPedidoConDetalles(pedido.getId());
            // Copiar las relaciones cargadas al pedido original si es necesario
        }

        return pedidos;
    }

}
