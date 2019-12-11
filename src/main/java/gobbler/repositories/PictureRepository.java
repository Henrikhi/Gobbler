package gobbler.repositories;

import gobbler.domain.Picture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {

    List<Picture> findByGobblerId(Long gobblerId);
    
    Picture findByGobblerIdAndIsProfilePicture(Long gobblerId, boolean isProfilePicture);
}
