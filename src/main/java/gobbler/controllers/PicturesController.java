package gobbler.controllers;

import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.io.IOException;
import java.util.List;
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

    @DeleteMapping("/delPicture/{id}")
    public String removePicture(@PathVariable Long id, Model model) {

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(SecurityContextHolder.getContext().
                getAuthentication().getName());

        if (pictureRepository.getOne(id) == null) {
            return "redirect:/home";
        }
        Picture picture = pictureRepository.getOne(id);
        if (!picture.isProfilePicture()) {
            pictureRepository.delete(picture);
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
        }

        return "redirect:/home";
    }

}
