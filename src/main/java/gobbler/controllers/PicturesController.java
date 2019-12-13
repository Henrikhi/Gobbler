package gobbler.controllers;

import gobbler.domain.Comment;
import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.CommentRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Controller
public class PicturesController {

    @Autowired
    private GobblerRepository gobblerRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private CommentRepository commentRepository;

    @ControllerAdvice
    public class MyErrorController extends ResponseEntityExceptionHandler {

        Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

        @ExceptionHandler(MultipartException.class)
        String handleFileException(HttpServletRequest request, Model model, Throwable ex) {
            model.addAttribute("message", "Error uploading a file. File size must not exceed 1MB.");
            return "error";
        }
    }

    @PostMapping("addPicture")
    public String addPhoto(Model model,
            @RequestParam("info") String info,
            @RequestParam("picture") MultipartFile file) throws IOException {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        String[] type = file.getContentType().split("/");

        if (type[0].equals("image")) {
            Picture picture = new Picture();
            picture.setContent(file.getBytes());
            if (info == null || info.trim().equals("")) {
                info = ("No info");
            } else {
                if (info.length() > 100) {
                    info = info.substring(0, 100);
                }
            }
            picture.setInfo(info);
            picture.setProfilePicture(false);

            List<Picture> pictures = pictureRepository.findByGobblerId(loggedGobbler.getId());
            if (pictures.size() < 10) {
                picture.setGobblerId(loggedGobbler.getId());
                pictureRepository.save(picture);
            }
        }

        return "redirect:/home";
    }

    @GetMapping(path = "/images/{id}/content", produces = "image/jpg")
    @ResponseBody
    public byte[] imageContent(@PathVariable Long id) {

        Picture picture = pictureRepository.getOne(id);
        return picture.getContent();
    }

    @GetMapping("/images/{id}")
    public String image(@PathVariable Long id, Model model) {
        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        model.addAttribute("loggedGobbler", loggedGobbler);
        model.addAttribute("picture", pictureRepository.findByGobblerIdAndIsProfilePicture(loggedGobbler.getId(), true));

        Optional<Picture> picture = pictureRepository.findById(id);
        if (picture.isPresent()) {
            model.addAttribute("picture", pictureRepository.getOne(id));

            if (picture.get().getGobblerId().equals(loggedGobbler.getId())) {
                return "ownPicture";
            } else {
                model.addAttribute("searchedGobbler", gobblerRepository.findById(picture.get().getGobblerId()));
                return "picture";
            }
        } else {
            return "redirect:/feed";
        }
    }

    @DeleteMapping("/delPicture/{id}")
    public String removePicture(@PathVariable Long id, Model model) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (pictureRepository.getOne(id) == null) {
            return "redirect:/home";
        }

        List<Picture> pictures = pictureRepository.findByGobblerId(loggedGobbler.getId());
        Picture picture = pictureRepository.getOne(id);
        if (pictures.contains(picture)) {
            if (!picture.isProfilePicture()) {
                pictureRepository.delete(picture);
            }
        }

        return "redirect:/home";
    }

    @Transactional
    @PostMapping("setProfilePicture/{id}")
    public String setProfilePicture(@PathVariable Long id, Model model) {

        if (id == null || pictureRepository.getOne(id) == null) {
            return "redirect:/home";
        }

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        List<Picture> pictures = pictureRepository.findByGobblerId(loggedGobbler.getId());

        Picture oldPic = null;
        Picture newPic = null;
        boolean picFound = false;

        for (int i = 0; i < pictures.size(); i++) {
            if (pictures.get(i).getId().equals(id)) {
                picFound = true;
                newPic = pictures.get(i);
            }
            if (pictures.get(i).isProfilePicture()) {
                oldPic = pictures.get(i);
            }
        }

        if (picFound) {
            oldPic.setProfilePicture(false);
            newPic.setProfilePicture(true);

            pictureRepository.save(oldPic);
            pictureRepository.save(newPic);

            loggedGobbler.setProfilePictureId(newPic.getId());
            gobblerRepository.save(loggedGobbler);
        }

        return "redirect:/home";
    }

    @PostMapping("/images/{id}/comment")
    public String postComment(Model model, @PathVariable Long id,
            @RequestParam("comment") String comment) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (comment == null || comment.trim().equals("")
                || pictureRepository.getOne(id) == null) {
            return "redirect:/feed";
        }

        if (comment.length() > 300) {
            comment = comment.substring(0, 300);
        }

        Comment newComment = new Comment();
        newComment.setComment(comment);
        newComment.setGobblerId(loggedGobbler.getId());
        newComment.setTime(LocalDateTime.now());
        commentRepository.save(newComment);

        Picture picture = pictureRepository.getOne(id);
        picture.addComment(newComment);
        pictureRepository.save(picture);

        return "redirect:/images/" + id;
    }

}
