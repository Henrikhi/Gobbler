package gobbler.controllers;

import gobbler.domain.Gobble;
import gobbler.domain.Gobbler;
import gobbler.domain.Picture;
import gobbler.repositories.GobbleRepository;
import gobbler.repositories.GobblerRepository;
import gobbler.repositories.PictureRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);
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

    @ControllerAdvice
    public class MyErrorController extends ResponseEntityExceptionHandler {

        Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

        @ExceptionHandler(MultipartException.class)
        String handleFileException(HttpServletRequest request, Model model, Throwable ex) {
            model.addAttribute("message", "Error uploading a file. File size must not exceed 1MB.");
            return "error";
        }
    }

    @PostMapping("/gobblers/{gobblerPath}/addPicture")
    public String addPhoto(@PathVariable String gobblerPath, Model model,
            @RequestParam("info") String info,
            @RequestParam("picture") MultipartFile file) throws IOException {

        System.out.println("You are about to upload a file thats size is " + file.getSize());
        Double size = Double.parseDouble("" + file.getSize());
        System.out.println("That is maybe " + size / 1024 / 1024 + " megabytes?");

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {

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
        }
        return "redirect:/gobblers/{gobblerPath}";
    }

    @GetMapping(path = "/gobblers/{gobblerPath}/images/{id}/content", produces = "image/png")
    @ResponseBody
    public byte[] imageContent(@PathVariable Long id, @PathVariable String gobblerPath) {
        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return null;
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }
            Picture picture = pictureRepository.getOne(id);

            List<Picture> pictures = pictureRepository.findByGobblerId(searchedGobbler.getId());

            if (pictures.contains(picture)) {
                return picture.getContent();
            }
        
        return null;
    }

    @DeleteMapping("/gobblers/{gobblerPath}/delPicture/{id}")
    public String removePicture(@PathVariable String gobblerPath,
            @PathVariable Long id, Model model) {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {

            if (pictureRepository.getOne(id) == null) {
                return "redirect:/gobblers/{gobblerPath}";
            }
            Picture picture = pictureRepository.getOne(id);
            if (!picture.isProfilePicture()) {
                pictureRepository.delete(picture);
            }
        }

        return "redirect:/gobblers/{gobblerPath}";
    }

    @Transactional
    @PostMapping("/gobblers/{gobblerPath}/setProfilePicture/{id}")
    public String setProfilePicture(@PathVariable String gobblerPath,
            @PathVariable Long id, Model model) {

        if (id == null) {
            return "redirect:/gobblers/{gobblerPath}";
        }

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {
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
        }

        return "redirect:/gobblers/{gobblerPath}";
    }

    @PostMapping("/gobblers/{gobblerPath}/gobbles")
    public String gobble(@PathVariable String gobblerPath, Model model,
            @RequestParam("gobble") String gobbleContent) {

        Gobbler searchedGobbler;
        if (gobblerRepository.findByGobblerPath(gobblerPath) == null) {
            return "redirect:/feed";
        } else {
            searchedGobbler = gobblerRepository.findByGobblerPath(gobblerPath);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Gobbler loggedGobbler = gobblerRepository.findByGobblerName(username);

        if (loggedGobbler.equals(searchedGobbler)) {

            if (gobbleContent == null || gobbleContent.trim().equals("")) {
                return "redirect:/feed";
            }

            if (gobbleContent.length() > 300) {
                gobbleContent = gobbleContent.substring(0, 300);
            }
            Gobble gobble = new Gobble();
            gobble.setContent(gobbleContent);
            gobble.setTime(LocalDateTime.now());
            gobble.setGobblerName(loggedGobbler.getGobblerName());
            gobbleRepository.save(gobble);
        }

        return "redirect:/feed";
    }

}
