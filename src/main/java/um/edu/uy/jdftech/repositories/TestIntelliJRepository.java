package um.edu.uy.jdftech.repositories;

import um.edu.uy.jdftech.entities.TestIntelliJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestIntelliJRepository extends JpaRepository<TestIntelliJ, Long> {}
