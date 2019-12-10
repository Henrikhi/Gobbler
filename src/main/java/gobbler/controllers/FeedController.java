package gobbler.controllers;

import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FeedController {

    @Autowired
    private GobbleRepository gobbleRepository;
    
    @Autowired
    private GobblerRepository gobblerRepository;

    @RequestMapping("/feed")
    public String feed(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler gobbler = gobblerRepository.findByGobblerName(username);

        model.addAttribute("gobbler", gobbler);
        
                
        List<Gobble> gobbles = new ArrayList<>();
        
        
        
        
        return "feed";
    }

}
