package gobbler.controllers;

import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.FollowRepository;
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

    @Autowired
    private FollowRepository followRepository;

    @RequestMapping("/feed")
    public String feed() {
        return "redirect:/feed/0";
    }

    @RequestMapping("/feed/{index}")
    public String feedIndex(Model model, @PathVariable String index) {
        int pageNum = 0;
        if (index != null) {
            try {
                pageNum = Integer.parseInt(index);
            } catch (NumberFormatException nfe) {
                return "redirect:/feed/0";
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());
        Picture profilePicture = pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true);

        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", profilePicture);

        Pageable pageable = PageRequest.of(pageNum, 25, Sort.by("dateTime").descending());

        List<Long> gobblerIds = new ArrayList<>();
        gobblerIds.add(loggedGobbler.getId());

        List<Gobbler> whoIFollow = followRepository.findWhoIFollow(loggedGobbler.getId());
        whoIFollow.forEach(gobbler -> {
            gobblerIds.add(gobbler.getId());
        });

        List<Gobble> gobbles = gobbleRepository.findByGobbler_IdIn(gobblerIds, pageable);

        model.addAttribute("gobbles", gobbles);

        return "feed";
    }

}
