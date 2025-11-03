package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Aderezo;

import java.util.List;

@Repository
public interface AderezoRepository extends JpaRepository<Aderezo, Long> {
    List<Aderezo> findByNombreContainingIgnoreCase(String nombre);
    List<Aderezo> findByPrecioBetween(Double minPrecio, Double maxPrecio);
}
