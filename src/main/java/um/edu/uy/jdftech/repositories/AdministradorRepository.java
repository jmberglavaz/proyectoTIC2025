package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Administrador;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findById(Long id);
    Optional<Administrador> findByEmail(String email);

    boolean existsById(Long id);

    @Query("SELECT a FROM Administrador a WHERE CONCAT(a.firstName, ' ', a.lastName) LIKE %:fullName%")
    List<Administrador> findByFullNameContaining(@Param("fullName") String fullName);
}
