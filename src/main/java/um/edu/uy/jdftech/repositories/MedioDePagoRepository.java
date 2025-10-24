package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import um.edu.uy.jdftech.entitites.MedioDePago;

public interface MedioDePagoRepository extends JpaRepository<MedioDePago, String> {
    // no tengo nada que agregar acá
}
