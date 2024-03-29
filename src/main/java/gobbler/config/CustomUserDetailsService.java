package gobbler.config;

import gobbler.domain.Gobbler;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import gobbler.repositories.GobblerRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private GobblerRepository gobblerRepository;


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
