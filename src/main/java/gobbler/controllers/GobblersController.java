package gobbler.controllers;

import gobbler.domain.Follow;
import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.repositories.FollowRepository;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private FollowRepository followRepository;

    @GetMapping("/gobblers/")
    public String allGobblers(Model model) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        model.addAttribute("gobblers", gobblerRepository.findAll());

        return "gobblers";

    }

    @RequestMapping("/gobblers/{gobblerPath}")
    public String gobbler(@PathVariable String gobblerPath, Model model) {

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

            List<Gobbler> followers = followRepository.findWhoFollowsMe(loggedGobbler.getId());
            List<Gobbler> following = followRepository.findWhoIFollow(loggedGobbler.getId());

            model.addAttribute("followers", followers);
            model.addAttribute("following", following);
            Pageable pageable = PageRequest.of(0, 10, Sort.by("time").descending());
            List<Gobble> gobbles = gobbleRepository.findByGobblerId(loggedGobbler.getId(), pageable);

            model.addAttribute("gobbles", gobbles);

            return "ownProfile";

        } else {
            model.addAttribute("searchedGobbler", searchedGobbler);
            model.addAttribute("pictures", pictureRepository.findByGobblerId(searchedGobbler.getId()));

            List<Gobbler> followers = followRepository.findWhoFollowsMe(searchedGobbler.getId());
            List<Gobbler> following = followRepository.findWhoIFollow(searchedGobbler.getId());

            model.addAttribute("followers", followers);
            model.addAttribute("following", following);
            Pageable pageable = PageRequest.of(0, 10, Sort.by("time").descending());
            List<Gobble> gobbles = gobbleRepository.findByGobblerId(searchedGobbler.getId(), pageable);

            model.addAttribute("gobbles", gobbles);

            Follow follow = followRepository.findByFollowerAndFollowing(loggedGobbler, searchedGobbler);
            if (follow == null) {
                model.addAttribute("IDontFollow", true);
            } else {
                model.addAttribute("IFollow", true);
            }
            follow = followRepository.findByFollowerAndFollowing(searchedGobbler, loggedGobbler);
            if (follow == null) {
//                model.addAttribute("DoesntFollowMe", true);
            } else {
                model.addAttribute("FollowsMe", true);
            }

            return "gobbler";
        }
    }

}
