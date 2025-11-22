package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Hamburguesa;

import java.util.Optional;

@Repository
public interface HamburguesaRepository extends JpaRepository<Hamburguesa, Long> {
    
    @Query("SELECT h FROM Hamburguesa h WHERE h.id_hamburguesa = :id")
    Optional<Hamburguesa> findByIdHamburguesa(@Param("id") Long id);
}