package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Bebida;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Pedido;
import um.edu.uy.jdftech.repositories.BebidaRepository;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.repositories.PedidoRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final BebidaRepository bebidaRepository;
    // falta agregar el resto

    public Pedido create(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new EntityNotFoundException("El cliente no existe"));
        Pedido pedido = new Pedido(cliente);
        return pedidoRepository.save(pedido);
    }

    public Pedido addDrink(Long pedidoId, Long bebidaId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + pedidoId));;
        Bebida bebida = bebidaRepository.findById(bebidaId).orElseThrow(() -> new EntityNotFoundException("Bebida no pudo hacer agregada"));
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
}
