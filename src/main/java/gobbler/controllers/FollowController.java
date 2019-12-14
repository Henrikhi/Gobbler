package gobbler.controllers;

import gobbler.domain.Follow;
import gobbler.domain.Gobbler;
import gobbler.repositories.FollowRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FollowController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @GetMapping("/follows")
    public String getFollows(Model model) {
        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        List<Follow> followers = followRepository.findByFollowing_id(loggedGobbler.getId());
        List<Follow> followings = followRepository.findByFollower_id(loggedGobbler.getId());

        model.addAttribute("followers", followers);
        model.addAttribute("followings", followings);

        return "follows";
    }

    @PostMapping("/follow/{id}")
    public String addFollow(Model model, @PathVariable Long id) {

        if (gobblerRepository.getOne(id) == null) {
            return "redirect:/home";
        }

        Gobbler gobbler = gobblerRepository.getOne(id);

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (loggedGobbler.equals(gobbler)) {
            return "redirect:/home";
        }

        if (!followRepository.findWhoIFollow(loggedGobbler.getId()).contains(id)) {
            Follow follow = new Follow();
            follow.setFollower(loggedGobbler);
            follow.setFollowing(gobbler);
            follow.setTime(LocalDateTime.now());
            followRepository.save(follow);
        }

        return "redirect:/gobblers/" + gobbler.getGobblerPath();
    }

    @PostMapping("/unfollow/{id}")
    public String removeFollow(Model model, @PathVariable Long id) {

        if (gobblerRepository.getOne(id) == null) {
            return "redirect:/home";
        }

        Gobbler gobbler = gobblerRepository.getOne(id);

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (loggedGobbler.equals(gobbler)) {
            return "redirect:/home";
        }

        Follow follow = followRepository.findByFollowerAndFollowing(loggedGobbler, gobblerRepository.getOne(id));
        if (follow == null) {
        } else {
            followRepository.delete(follow);
        }

        return "redirect:/gobblers/" + gobbler.getGobblerPath();
    }

    @PostMapping("/block/{id}")
    public String block(Model model, @PathVariable Long id) {

        if (gobblerRepository.getOne(id) == null) {
            return "redirect:/home";
        }

        Gobbler gobbler = gobblerRepository.getOne(id);

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (loggedGobbler.equals(gobbler)) {
            return "redirect:/home";
        }

        Follow follow = followRepository.findByFollowerAndFollowing(gobblerRepository.getOne(id), loggedGobbler);
        if (follow == null) {
        } else {
            followRepository.delete(follow);
        }

        return "redirect:/gobblers/" + gobbler.getGobblerPath();
    }

}
