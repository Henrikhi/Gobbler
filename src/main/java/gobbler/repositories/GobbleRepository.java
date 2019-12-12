package gobbler.repositories;

import gobbler.domain.Gobble;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GobbleRepository extends JpaRepository<Gobble, Long> {
    
    List<Gobble> findByGobblerNameIn(Collection<String> GobblerNames, Pageable pageable);

}
