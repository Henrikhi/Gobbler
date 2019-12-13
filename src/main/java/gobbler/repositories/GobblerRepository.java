package gobbler.repositories;

import gobbler.domain.Gobbler;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GobblerRepository extends JpaRepository<Gobbler, Long> {

    Gobbler findByGobblerName(String gobblerName);

    Gobbler findByGobblerPath(String gobblerPath);

    List<Gobbler> findByGobblerNameIgnoreCaseContaining(String search);

    List<Gobbler> findByNameIgnoreCaseContaining(String search);

    List<Gobbler> findByGobblerPathIgnoreCaseContaining(String search);
    
    List<Gobbler> findByIdIn(Collection<Long> ids);
}
