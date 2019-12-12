
package gobbler.repositories;

import gobbler.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FollowRepository extends JpaRepository<Follow, Long>{
    
}
