package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Pedido;
import um.edu.uy.jdftech.entitites.Topping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToppingRepository extends JpaRepository<Topping, Long> {
    Optional<Topping> findByIdTopping(Long idTopping);

    @Query("SELECT t FROM Topping t ORDER BY t.idTopping DESC LIMIT 10")
    List<Topping> encontrarUltimos10ToppingsAgregados();

    @Query("SELECT t FROM Topping t WHERE t.tipo = :tipo ORDER BY t.idTopping")
    List<Topping> encontrarToppingsDeTipo(@Param("tipo") char tipo);

    @Query("SELECT t FROM Topping t WHERE t.fechaAgregado BETWEEN :from AND :to ORDER BY t.fechaAgregado DESC")
    List<Topping> encontrarDesdeHastaFecha(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT t FROM Topping t WHERE t.nombre LIKE %:nombre%")
    List<Topping> findByNombreContainingIgnoreCase(@Param("nombreTopping") String nombre);

    @Query("SELECT t FROM Topping t WHERE t.tipo = :tipo AND t.hamburguesaOPizza = :producto")
    List<Topping> findByTipoAndHamburguesaOPizza(@Param("tipo") char tipo, @Param("producto") char producto);
}
