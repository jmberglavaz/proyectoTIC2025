package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Acompanamiento;
import um.edu.uy.jdftech.entitites.Bebida;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Pedido;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.repositories.AcompanamientoRepository;
import um.edu.uy.jdftech.repositories.BebidaRepository;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.repositories.PedidoRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));;
        Bebida bebida = bebidaRepository.findById(bebidaId).orElseThrow(() -> new EntityNotFoundException("Bebida no pudo ser agregada"));
        pedido.getBebidas().add(bebida);
        bebida.getPedidos().add(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido removeDrink(Long pedidoId, Long bebidaId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));;
        Bebida bebida = bebidaRepository.findById(bebidaId).orElseThrow(() -> new EntityNotFoundException("Bebida no fue encontrada para eliminarla"));
        pedido.getBebidas().remove(bebida);
        bebida.getPedidos().remove(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido agregarAcompanamiento(Long pedidoId, Long acompanamientoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));;
        Acompanamiento acompanamiento = acompanamientoRepository.findById(acompanamientoId).orElseThrow(() -> new EntityNotFoundException("Acompañamiento no pudo ser agregado"));
        pedido.getAcompanamientos().add(acompanamiento);
        acompanamiento.getPedidos().add(pedido);
        pedido.calculateTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido removeAcompanamiento(Long pedidoId, Long acompanamientoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));;
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

    public Pedido updateStatus(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));
        pedido.setStatus(nuevoEstado);
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

    public List<Pedido> findWithFilters(String numero, String clienteId, String estado,
                                        String tipo, String desde, String hasta) {
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
}
