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
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = obtenerPedidoPorId(pedidoId);
        
        if (pedido.getStatus() != EstadoPedido.EN_COLA) {
            throw new RuntimeException("Solo se pueden cancelar pedidos en estado EN_COLA");
        }
        
        // Eliminar el pedido
        pedidoRepository.delete(pedido);
    }

    // Obtener todos los pedidos de un cliente
    public List<Pedido> obtenerPedidosCliente(Cliente cliente) {
        return pedidoRepository.findByClientIdOrderByDateDesc(cliente.getId());
    }
    // Obtener el pedido activo más reciente de un cliente
    public Optional<Pedido> obtenerPedidoActivo(Cliente cliente) {
        List<Pedido> pedidosActivos = pedidoRepository.findPedidosActivosByClientId(cliente.getId());
        return pedidosActivos.isEmpty() ? Optional.empty() : Optional.of(pedidosActivos.get(0));
    }
}