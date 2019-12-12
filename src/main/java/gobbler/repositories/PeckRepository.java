
package gobbler.repositories;

import gobbler.domain.Peck;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PeckRepository extends JpaRepository<Peck, Long>{
    
}
