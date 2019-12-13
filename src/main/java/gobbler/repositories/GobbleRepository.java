package gobbler.repositories;

import gobbler.domain.Gobble;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GobbleRepository extends JpaRepository<Gobble, Long> {

    List<Gobble> findByGobblerIdIn(Collection<Long> GobblerIds, Pageable pageable);

    List<Gobble> findByGobblerId(Long GobblerId, Pageable pageable);

}
