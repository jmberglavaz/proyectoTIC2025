package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.MedioDePago;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedioDePagoRepository extends JpaRepository<MedioDePago, Long> {
    
    // Buscar todos los medios de pago de un cliente
    List<MedioDePago> findByCliente(Cliente cliente);
    
    // Buscar medios de pago por ID de cliente
    List<MedioDePago> findByClienteId(Long clienteId);
    
    // Buscar por número de tarjeta
    Optional<MedioDePago> findByCardNumber(Long cardNumber);
}