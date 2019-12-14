package gobbler.controllers;

import gobbler.domain.Comment;
import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.repositories.CommentRepository;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GobblesController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private GobbleRepository gobbleRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/gobbles")
    public String postGobble(Model model,
            @RequestParam("gobble") String gobbleContent) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.
                getContext().getAuthentication().getName());

        if (gobbleContent == null || gobbleContent.trim().equals("")) {
            return "redirect:/feed";
        }

        if (gobbleContent.length() > 300) {
            gobbleContent = gobbleContent.substring(0, 300);
        }
        Gobble gobble = new Gobble();
        gobble.setContent(gobbleContent);
        gobble.setTime(LocalDateTime.now());
        gobble.setGobbler(loggedGobbler);
        gobbleRepository.save(gobble);

        return "redirect:/feed";
    }

    @GetMapping("/gobbles/{id}")
    public String getGobble(Model model, @PathVariable String id) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());
        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        Long searchedId = -1L;

        if (id != null) {
            try {
                searchedId = Long.parseLong(id);
            } catch (NumberFormatException nfe) {
                return "redirect:/feed";
            }
        }

        Optional<Gobble> gobble = gobbleRepository.findById(searchedId);
        if (gobble.isPresent()) {
            model.addAttribute("gobble", gobbleRepository.getOne(searchedId));
            return "gobble";
        } else {
            return "redirect:/feed";
        }
    }

    @PostMapping("/gobbles/{id}/comment")
    public String postComment(Model model, @PathVariable Long id,
            @RequestParam("comment") String comment) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (comment == null || comment.trim().equals("")
                || gobbleRepository.getOne(id) == null) {
            return "redirect:/feed";
        }

        if (comment.length() > 300) {
            comment = comment.substring(0, 300);
        }

        Comment newComment = new Comment();
        newComment.setComment(comment);
        newComment.setGobbler(loggedGobbler);
        newComment.setTime(LocalDateTime.now());
        commentRepository.save(newComment);

        Gobble gobble = gobbleRepository.getOne(id);
        gobble.addComment(newComment);
        gobbleRepository.save(gobble);

        return "redirect:/gobbles/" + gobble.getId();
    }

    @PostMapping("/gobbles/{id}/peck")
    public String peck(Model model, @PathVariable Long id) {

        System.out.println("testi1");

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        System.out.println("testi2");

        if (gobbleRepository.getOne(id) == null) {
            return "redirect:/feed";
        }

        System.out.println("testi3");

        Gobble gobble = gobbleRepository.getOne(id);
        gobble.peck(loggedGobbler);
        gobbleRepository.save(gobble);

        System.out.println("testi4");

        return "redirect:/gobbles/" + id;
    }

    @PostMapping("/gobbles/{id}/unpeck")
    public String unpeck(Model model, @PathVariable Long id) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (gobbleRepository.getOne(id) == null) {
            return "redirect:/feed";
        }

        Gobble gobble = gobbleRepository.getOne(id);
        gobble.unpeck(loggedGobbler);
        gobbleRepository.save(gobble);

        return "redirect:/gobbles/" + id;
    }

    @GetMapping("/gobbles/{id}/pecks")
    public String pecks(Model model, @PathVariable Long id) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());
        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        if (gobbleRepository.getOne(id) == null) {
            return "redirect:/feed";
        }

        Gobble gobble = gobbleRepository.getOne(id);
        List<Gobbler> peckers = gobble.getPeckers();
        model.addAttribute("isGobble", true);
        model.addAttribute("isPicture", false);
        model.addAttribute("gobble", gobble);
        model.addAttribute("peckers", peckers);

        return "peckers";
    }

}
