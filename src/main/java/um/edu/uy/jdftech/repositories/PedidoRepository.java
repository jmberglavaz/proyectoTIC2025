package um.edu.uy.jdftech.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Pedido;
import um.edu.uy.jdftech.enums.EstadoPedido;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClientId(Long clientId);

    // Buscar todos los pedidos de un cliente ordenados por fecha DESC
    List<Pedido> findByClientIdOrderByDateDesc(Long clientId);

    List<Pedido> findByDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clienteId ORDER BY p.date DESC")
    List<Pedido> getLast3OrdersByClient(@Param("clientId") Long clienteId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clientId AND p.date BETWEEN :from AND :to ORDER BY p.date DESC")
    List<Pedido> findHistoricByClient(@Param("clientId") Long clienteId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<Pedido> findByStatusIn(List<EstadoPedido> estados);

    @Query("SELECT p FROM Pedido p WHERE p.status IN :estados ORDER BY p.date ASC")
    List<Pedido> findByStatusInOrderByDate(@Param("estados") List<EstadoPedido> estados);

    @Query("SELECT p FROM Pedido p WHERE " +
            "(:numero IS NULL OR p.id = :numero) AND " +
            "(:clienteId IS NULL OR p.client.id = :clienteId) AND " +
            "(:estado IS NULL OR p.status = :estado) AND " +
            "(:desde IS NULL OR p.date >= :desde) AND " +
            "(:hasta IS NULL OR p.date <= :hasta) " +
            "ORDER BY p.date DESC")
    List<Pedido> findWithFilters(@Param("numero") Long numero,
                                 @Param("clienteId") Long clienteId,
                                 @Param("estado") EstadoPedido estado,
                                 @Param("desde") LocalDateTime desde,
                                 @Param("hasta") LocalDateTime hasta);

    @Query("SELECT p FROM Pedido p WHERE " +
            "p.status IN :estados AND " +
            "(:numero IS NULL OR p.id = :numero) AND " +
            "(:clienteId IS NULL OR p.client.id = :clienteId) AND " +
            "(:desde IS NULL OR p.date >= :desde) AND " +
            "(:hasta IS NULL OR p.date <= :hasta) " +
            "ORDER BY p.date DESC")
    List<Pedido> findActivosWithFilters(@Param("estados") List<EstadoPedido> estados,
                                        @Param("numero") Long numero,
                                        @Param("clienteId") Long clienteId,
                                        @Param("desde") LocalDateTime desde,
                                        @Param("hasta") LocalDateTime hasta);

    @Query("SELECT p FROM Pedido p ORDER BY p.date DESC")
    List<Pedido> findLast10Orders(Pageable pageable);

    // Buscar pedidos ACTIVOS de un cliente (EN_COLA, EN_PREPARACION, EN_CAMINO)
    @Query("SELECT p FROM Pedido p WHERE p.client.id = :clientId AND p.status IN (um.edu.uy.jdftech.enums.EstadoPedido.EN_COLA, um.edu.uy.jdftech.enums.EstadoPedido.EN_PREPARACION, um.edu.uy.jdftech.enums.EstadoPedido.EN_CAMINO) ORDER BY p.date DESC")
    List<Pedido> findPedidosActivosByClientId(@Param("clientId") Long clientId);

    @Query("SELECT p FROM Pedido p " +
            "LEFT JOIN FETCH p.direccion " +
            "LEFT JOIN FETCH p.medioDePago " +
            "LEFT JOIN FETCH p.client " +
            "WHERE p.id = :pedidoId")
    Optional<Pedido> findByIdWithDetails(@Param("pedidoId") Long pedidoId);
}