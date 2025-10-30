package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Cliente;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findById(Long id);

    boolean existsById(Long id);


    @Query("SELECT c FROM Cliente c WHERE CONCAT(c.firstName, ' ', c.lastName) LIKE %:fullName%")
    List<Cliente> findByFullNameContaining(@Param("fullName") String fullName);
}
