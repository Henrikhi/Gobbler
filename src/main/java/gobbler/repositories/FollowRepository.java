package gobbler.repositories;

import gobbler.domain.Follow;
import gobbler.domain.Gobbler;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT following FROM Follow WHERE follower_id = :id")
    List<Gobbler> findWhoIFollow(@Param("id") Long id);

    @Query("SELECT follower FROM Follow WHERE following_id = :id")
    List<Gobbler> findWhoFollowsMe(@Param("id") Long id);

    List<Follow> findByFollowing_id(Long id);

    List<Follow> findByFollower_id(Long id);

    Follow findByFollowerAndFollowing(Gobbler follower, Gobbler following);

}
