package gobbler.repositories;

import gobbler.domain.Gobble;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GobbleRepository extends JpaRepository<Gobble, Long> {

}
