package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entities.Pizza;

import java.util.List;
@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    List<Pizza> encontrarPizzasPorNombre(String nombre);
}
