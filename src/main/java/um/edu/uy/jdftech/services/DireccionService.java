// DireccionService.java
package um.edu.uy.jdftech.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Direccion;
import um.edu.uy.jdftech.repositories.DireccionRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DireccionService {
    private final DireccionRepository direccionRepository;
    private final ClienteService clienteService;

    public List<Direccion> findByCliente(Long clienteId) {
        return direccionRepository.findByClienteId(clienteId);
    }

    public Direccion save(Direccion direccion, Long clienteId) {
        Cliente cliente = clienteService.findById(clienteId);
        direccion.setCliente(cliente);
        return direccionRepository.save(direccion);
    }

    public void delete(Long direccionId, Long clienteId) {
        Direccion direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (!direccion.getCliente().getId().equals(clienteId)) {
            throw new RuntimeException("No autorizado");
        }

        direccionRepository.delete(direccion);
    }

    public Direccion setDefault(Long direccionId, Long clienteId) {
        // Primero, quitar default de todas las direcciones del cliente
        List<Direccion> direcciones = findByCliente(clienteId);
        for (Direccion dir : direcciones) {
            if (dir.isDefect()) {
                dir.setDefect(false);
                direccionRepository.save(dir);
            }
        }

        // Luego, establecer la nueva dirección como default
        Direccion nuevaDefault = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        if (!nuevaDefault.getCliente().getId().equals(clienteId)) {
            throw new RuntimeException("No autorizado");
        }

        nuevaDefault.setDefect(true);
        return direccionRepository.save(nuevaDefault);
    }
}