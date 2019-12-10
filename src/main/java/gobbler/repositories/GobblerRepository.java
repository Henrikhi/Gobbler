package gobbler.repositories;

import gobbler.domain.Gobbler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GobblerRepository extends JpaRepository<Gobbler, Long> {

    Gobbler findByGobblerName(String gobblerName);

    Gobbler findByGobblerPath(String gobblerPath);
}
