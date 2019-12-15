package gobbler.controllers;

import gobbler.domain.Gobbler;
import gobbler.repositories.GobblerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @RequestMapping("*")
    public String handleDefault() {
        return "redirect:/feed";
    }

    @RequestMapping("/home")
    public String home(Model model) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());
        
        
        model.addAttribute("loggedGobbler", loggedGobbler);

        String gobblerPath = loggedGobbler.getGobblerPath();

        return "redirect:/gobblers/" + gobblerPath;
    }
}
