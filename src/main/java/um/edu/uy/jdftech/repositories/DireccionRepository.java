package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Direccion;
import um.edu.uy.jdftech.entitites.Usuario;

import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    List<Direccion> findByUsuario(Usuario usuario);

    List<Direccion> findByStreetNameContainingIgnoreCaseAndDoorNumberContainingIgnoreCase(
            String streetName, String doorNumber);
}