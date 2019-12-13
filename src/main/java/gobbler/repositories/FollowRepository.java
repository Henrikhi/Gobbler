package gobbler.repositories;

import gobbler.domain.Follow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

//    @Query("SELECT f.followingId FROM Follow f JOIN Gobbler g ON g.id = f.followerId WHERE f.followerId = :id ")
//    List<String> findWhoIFollow(@Param("id") Long id);
    
    
    @Query("SELECT g.id FROM Gobbler g JOIN Follow f ON g.id = f.followingId WHERE f.followerId = :id ")
    List<Long> findWhoIFollow(@Param("id") Long id);

     @Query("SELECT g.id FROM Gobbler g JOIN Follow f ON g.id = f.followerId WHERE f.followingId = :id ")
    List<Long> findWhoFollowsMe(@Param("id") Long id);
    
}
