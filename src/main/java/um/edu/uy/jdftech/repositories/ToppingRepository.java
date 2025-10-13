package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entities.Topping;

import java.util.List;
@Repository
public interface ToppingRepository extends JpaRepository<Topping, Long> {
    List<Topping> encontrarToppingsPorNombre(String nombre);
}
