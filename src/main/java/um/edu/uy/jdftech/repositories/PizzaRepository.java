package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Pizza;

import java.util.Optional;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    
    @Query("SELECT p FROM Pizza p WHERE p.id_pizza = :id")
    Optional<Pizza> findByIdPizza(@Param("id") Long id);
}