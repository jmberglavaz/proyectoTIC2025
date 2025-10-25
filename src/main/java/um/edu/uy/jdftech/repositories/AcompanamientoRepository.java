package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Acompanamiento;

import java.util.List;
@Repository
public interface AcompanamientoRepository extends JpaRepository<Acompanamiento, Long> {
    List<Acompanamiento> findBySize(String tamano);
    List<Acompanamiento> findByNameContainingIgnoreCase(String name);
    List<Acompanamiento> findByPriceBetween(Double lower, Double higher);
}
