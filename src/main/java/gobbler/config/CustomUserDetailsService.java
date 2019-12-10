package gobbler.config;

import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
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

    @PostConstruct
    public void init() throws IOException {

        if (gobblerRepository.findByGobblerName("kalkkuna") == null) {

            Gobbler gobbler = new Gobbler();
            gobbler.setGobblerName("kalkkuna");
            gobbler.setName("herra kalkkuna");
            gobbler.setGobblerPath("kalkkuna");
            gobbler.setPassword(passwordEncoder.encode("kalkkuna"));

            Picture picture = new Picture();
            picture.setInfo("Default gobbler picture.");

            String workingDirectory = System.getProperty("user.dir");
            System.out.println(workingDirectory);

            File file = new File("./src/main/resources/public/defaultPic.png");

            byte[] fileContent = Files.readAllBytes(file.toPath());
            picture.setContent(fileContent);

            pictureRepository.save(picture);

            gobbler.addPicture(picture);
            gobbler.setProfilePicture(picture);

            gobblerRepository.save(gobbler);

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
