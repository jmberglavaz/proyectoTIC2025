package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Carrito;
import um.edu.uy.jdftech.entitites.Cliente;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    // Buscar carrito por cliente
    Optional<Carrito> findByCliente(Cliente cliente);
    
    // Buscar carrito por ID de cliente
    Optional<Carrito> findByClienteId(Long clienteId);
    
    // Verificar si existe carrito para un cliente
    boolean existsByCliente(Cliente cliente);
}