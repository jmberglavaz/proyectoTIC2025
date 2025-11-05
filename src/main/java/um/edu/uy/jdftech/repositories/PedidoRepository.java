package um.edu.uy.jdftech.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Pedido;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Buscar todos los pedidos de un cliente ordenados por fecha DESC
    List<Pedido> findByClientIdOrderByDateDesc(Long clientId);
    
    List<Pedido> findByDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clienteId ORDER BY p.date DESC")
    List<Pedido> getLast3OrdersByClient(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clientId AND p.date BETWEEN :from AND :to ORDER BY p.date DESC")
    List<Pedido> findHistoricByClient(@Param("clienteId") Long clienteId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    
    // Buscar pedidos ACTIVOS de un cliente (EN_COLA, EN_PREPARACION, EN_CAMINO)
    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clientId AND p.status IN (um.edu.uy.jdftech.enums.EstadoPedido.EN_COLA, um.edu.uy.jdftech.enums.EstadoPedido.EN_PREPARACION, um.edu.uy.jdftech.enums.EstadoPedido.EN_CAMINO) ORDER BY p.date DESC")
    List<Pedido> findPedidosActivosByClientId(@Param("clientId") Long clientId);
}