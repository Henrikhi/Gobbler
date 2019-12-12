package gobbler.controllers;

import gobbler.domain.Gobbler;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GobblersController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private GobbleRepository gobbleRepository;

    @RequestMapping("/gobblers/{gobblerPath}")
    public String gobblers(@PathVariable String gobblerPath, Model model) {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        if (loggedGobbler.equals(searchedGobbler)) {
            model.addAttribute("pictures", pictureRepository.findByGobblerId(loggedGobbler.getId()));
            return "ownProfile";
        } else {
            model.addAttribute("searchedGobbler", searchedGobbler);
            model.addAttribute("pictures", pictureRepository.findByGobblerId(searchedGobbler.getId()));
            return "gobblers";
        }
    }

}
