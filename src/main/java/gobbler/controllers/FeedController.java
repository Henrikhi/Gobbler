package gobbler.controllers;

import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FeedController {

    @Autowired
    private GobbleRepository gobbleRepository;

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @RequestMapping("/feed")
    public String feed(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler gobbler = gobblerRepository.findByGobblerName(username);

        model.addAttribute("loggedGobbler", gobbler);

        Picture profilePicture = pictureRepository.findByGobblerIdAndIsProfilePicture(gobbler.getId(), true);
        model.addAttribute("picture", profilePicture);

        Pageable pageable = PageRequest.of(0, 25, Sort.by("time").descending());
        List<String> gobblerNames = new ArrayList<>();
        gobblerNames.add(gobbler.getGobblerName());
        
        //ADD THE IDS OF THE ACCOUNTS THE ACCOUNT FOLLOWS FOR THEIR POSTS AS WELL
        
        List<Gobble> gobbles = gobbleRepository.findByGobblerNameIn(gobblerNames, pageable);
        
        model.addAttribute("gobbles", gobbles);

        return "feed";
    }
    
    @RequestMapping("/feed/{index}")
    public String feedIndex(Model model, @PathVariable int index) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler gobbler = gobblerRepository.findByGobblerName(username);

        model.addAttribute("gobbler", gobbler);

        Picture profilePicture = pictureRepository.findByGobblerIdAndIsProfilePicture(gobbler.getId(), true);
        model.addAttribute("picture", profilePicture);

        Pageable pageable = PageRequest.of(index, 25, Sort.by("time").descending());
        List<String> gobblerNames = new ArrayList<>();
        gobblerNames.add(gobbler.getGobblerName());
        
        //ADD THE IDS OF THE ACCOUNTS THE ACCOUNT FOLLOWS FOR THEIR POSTS AS WELL
        
        List<Gobble> gobbles = gobbleRepository.findByGobblerNameIn(gobblerNames, pageable);
        
        model.addAttribute("gobbles", gobbles);

        return "feed";
    }

}
