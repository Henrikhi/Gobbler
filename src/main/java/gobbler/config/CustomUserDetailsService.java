package gobbler.config;

import gobbler.domain.Follow;
import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.FollowRepository;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import javax.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PictureRepository pictureRepository;
    
    @Autowired
    private FollowRepository followRepository;

    @PostConstruct
    public void init() throws IOException {

        if (gobblerRepository.findByGobblerName("kalkkuna") == null) {

            Gobbler gobbler = new Gobbler();
            gobbler.setGobblerName("kalkkuna");
            gobbler.setName("herra kalkkuna");
            gobbler.setGobblerPath("kalkkuna");
            gobbler.setPassword(passwordEncoder.encode("kalkkuna"));
            gobblerRepository.save(gobbler);

            Gobbler gobbler2 = new Gobbler();
            gobbler2.setGobblerName("kalkkuna2");
            gobbler2.setName("rouva kalkkuna2");
            gobbler2.setGobblerPath("kalkkuna2");
            gobbler2.setPassword(passwordEncoder.encode("kalkkuna2"));
            gobblerRepository.save(gobbler2);
            
            Follow follow = new Follow();
            follow.setFollowerId(gobbler.getId());
            follow.setFollowingId(gobbler2.getId());
            follow.setTime(LocalDateTime.now());
            followRepository.save(follow);
            

            String workingDirectory = System.getProperty("user.dir");
            File file = new File("./src/main/resources/public/defaultPic.png");
            byte[] fileContent = Files.readAllBytes(file.toPath());

            Picture picture = new Picture();
            picture.setInfo("Default gobbler picture.");
            picture.setContent(fileContent);
            picture.setProfilePicture(true);
            picture.setGobblerId(gobbler.getId());
            pictureRepository.save(picture);

            Picture picture2 = new Picture();
            picture2.setInfo("Default gobbler picture.");
            picture2.setContent(fileContent);
            picture2.setProfilePicture(true);
            picture2.setGobblerId(gobbler2.getId());
            pictureRepository.save(picture2);
            
            gobbler.setProfilePictureId(picture.getId());
            gobbler2.setProfilePictureId(picture2.getId());
            gobblerRepository.save(gobbler);
            gobblerRepository.save(gobbler2);
            

        }
    }

    @Override
    public UserDetails loadUserByUsername(String gobblerName) throws UsernameNotFoundException {
        Gobbler account = gobblerRepository.findByGobblerName(gobblerName);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + gobblerName);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getGobblerName(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
