package gobbler.repositories;

import gobbler.domain.Follow;
import gobbler.domain.Gobbler;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

//    @Query("SELECT f.followingId FROM Follow f JOIN Gobbler g ON g.id = f.followerId WHERE f.followerId = :id ")
//    List<String> findWhoIFollow(@Param("id") Long id);
//    @Query("SELECT g.id FROM Gobbler g JOIN Follow f ON g.id = f.followingId WHERE f.followerId = :id ")
    @Query("SELECT following FROM Follow WHERE follower_id = :id")
    List<Gobbler> findWhoIFollow(@Param("id") Long id);

//     @Query("SELECT g.id FROM Gobbler g JOIN Follow f ON g.id = f.followerId WHERE f.followingId = :id ")
    @Query("SELECT follower FROM Follow WHERE following_id = :id")
    List<Gobbler> findWhoFollowsMe(@Param("id") Long id);

    List<Follow> findByFollowing_id(Long id);

    List<Follow> findByFollower_id(Long id);

    Follow findByFollowerAndFollowing(Gobbler follower, Gobbler following);

//    @Query("SELECT "
//            + "    FollowerGobblers(g.id, f.time) "
//            + "FROM "
//            + "    Follow f JOIN Gobbler g ON g.id = f.followerId WHERE f.followingId = 2")
//    List<FollowerGobblers> findFollowerGobblers(@Param("id") Long id);
//
//    public class FollowerGobblers {
//
//        private Gobbler follower;
//        private LocalDateTime time;
//
//        public FollowerGobblers(Gobbler follower, LocalDateTime time) {
//            this.follower = follower;
//            this.time = time;
//        }
//    }
//
//     @Query("SELECT "
//            + "    FollowingGobblers(g, f.time) "
//            + "FROM "
//            + "    Follow f JOIN Gobbler g ON g.id = f.followingId WHERE f.followerId = :id ")
//    List<FollowingGobblers> findFollowingGobblers(@Param("id") Long id);
//
//    public class FollowingGobblers {
//
//        private Gobbler following;
//        private LocalDateTime time;
//
//        public FollowingGobblers(Gobbler following, LocalDateTime time) {
//
//            this.following = following;
//            this.time = time;
//        }
//    }
}
