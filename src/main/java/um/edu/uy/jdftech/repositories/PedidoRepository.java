package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Pedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByFechaBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clienteId ORDER BY p.date DESC LIMIT 3")
    List<Pedido> getLast3OrdersByClient(Long clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clienteId AND p.date BETWEEN :from AND :to ORDER BY p.date DESC")
    List<Pedido> findHistoricByClient(Long clienteId, LocalDateTime from, LocalDateTime to);
}
