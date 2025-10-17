package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Bebida;

import java.util.List;

@Repository
public interface BebidaRepository extends JpaRepository<Bebida, Long> {
    List<Bebida> findBySize(String tamano);
    List <Bebida> findByNameContainingIgnoreCase(String name);
    List<Bebida> findByPriceBetween(Double lower, Double higher);
}
