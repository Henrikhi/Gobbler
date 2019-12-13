package gobbler.controllers;

import gobbler.domain.Gobbler;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @GetMapping("/search/{entry}")
    public String search(@PathVariable String entry, Model model) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        model.addAttribute("gobblersByGobblerName", gobblerRepository.findByGobblerNameIgnoreCaseContaining(entry));
        model.addAttribute("gobblersByName", gobblerRepository.findByNameIgnoreCaseContaining(entry));
        model.addAttribute("gobblersByGobblerPath", gobblerRepository.findByGobblerPathIgnoreCaseContaining(entry));
        return "search";
    }

    @GetMapping("/search")
    public String search2(@RequestParam String entry) {

        if (entry == null || entry.trim().equals("")) {
            return "redirect:/gobblers/";
        }

        return "redirect:/search/" + entry;
    }

}
