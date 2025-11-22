package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Direccion;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    
    // Buscar todas las direcciones de un cliente
    List<Direccion> findByCliente(Cliente cliente);
    
    // Buscar direcciones por ID de cliente
    List<Direccion> findByClienteId(Long clienteId);
}